package org.genyris.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.HTMLFormatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.format.JSONFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class GenyrisHTTPD extends NanoHTTPD {

    static HashMap serverSockets = new HashMap();
    Interpreter interpreter;
    String filename;
    Exp[] argv;

    Symbol NIL;

    Dictionary HttpRequestClazz, AlistClazz;

    public GenyrisHTTPD(int port, String filename, Exp[] argv)
            throws GenyrisException {
        myTcpPort = port;
        this.filename = filename;
        this.argv = argv;

        try {
            ss = getSharedServerSocket(myTcpPort);
        } catch (IOException e1) {
            throw new GenyrisException("GenyrisHTTPD: Port " + myTcpPort + " "
                    + e1.getMessage());
        }
    }

    private static synchronized ServerSocket getSharedServerSocket(int port)
            throws IOException {
        if (serverSockets.containsKey(Integer.valueOf(port))) {
            return (ServerSocket) serverSockets.get(Integer.valueOf(port));
        } else {
            ServerSocket ss = new ServerSocket(port);
            ss.setSoTimeout(SERVER_SOCKET_TIMEOUT);
            serverSockets.put(Integer.valueOf(port), ss);
            return ss;
        }
    }

    private static Exp makeListOfArray(Symbol NIL, Exp[] args) {
        // TODO DRY - repeated in evaluater somewhere...
        Exp arglist = NIL;
        for (int i = args.length - 1; i > 0; i--) {
            arglist = new Pair(args[i], arglist);
        }
        return arglist;
    }

    public Thread run() throws IOException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                boolean terminating = false;
                try {
                    interpreterSetup();
                } catch (GenyrisException e) {
                    System.out.println("GenyrisHTTPD: " + e.getMessage());
                    return;
                }
                while (!terminating) {
                    try {
                        Thread.yield();
                        new HTTPSession(ss.accept());
                    } catch (InterruptedIOException e) {
                        if (Thread.currentThread().isInterrupted()) {
                            terminating = true;
                        }
                        continue;
                    } catch (IOException ioe) {
                        System.out.println("GenyrisHTTPD: IOException "
                                + ioe.getMessage());
                    }
                }
                // try {
                // if (ss != null)
                // ; // ss.close();
                // } catch (IOException e) {
                // }

            }
        });
        t.setName(this.getClass().getName() + " " + myTcpPort + " "
                + this.filename);
        t.setDaemon(true);
        t.start();
        return t;
    }

    public synchronized NanoResponse serve(long sessionNumber, Socket sock,
            String uri, String method, Properties header, Properties parms,
            String rootdir, String clientIP) {
        Exp request = NIL;
        // System.out.println(method + " '" + uri + "' ");

        Exp headers = NIL;
        Enumeration e = header.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            headers = new Pair(new Pair(new StrinG(value), new StrinG(header
                    .getProperty(value))), headers);
            // System.out.println(" HDR: '" + value + "' = '" +
            // header.getProperty(value) + "'");
        }
        headers.addClass(AlistClazz);

        Exp parameters = NIL;
        e = parms.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            parameters = new Pair(new Pair(new StrinG(value), new StrinG(parms
                    .getProperty(value))), parameters);
            // System.out.println(" PRM: '" + value + "' = '" +
            // parms.getProperty(value) + "'");
        }
        parameters.addClass(this.AlistClazz);

        request = new Pair(new Bignum(sessionNumber), request);
        request = new Pair(new Bignum(sock.getPort()), request);
        request = new Pair(new Pair(new StrinG(clientIP), NIL), request);
        request = new Pair(parameters, request);
        request = new Pair(headers, request);
        request = new Pair(new StrinG(uri), request);
        request = new Pair(new StrinG(method), request);
        request.addClass(HttpRequestClazz);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Writer output = new PrintWriter(buffer);
        // (httpd-serve request)
        Exp expression = new Pair(interpreter.intern("httpd-serve"), new Pair(
                request, NIL));

        try {
            Formatter formatter;
            // formatter = new IndentedFormatter(output, 1, interpreter);
            // expression.acceptVisitor(formatter);
            Exp result = interpreter.evalInGlobalEnvironment(expression);
            String status = result.nth(0, NIL).toString();
            if (status.equals("SERVE-FILE")) {
                // This response from Genyris means web server serves a static
                // file
                String rootDirectory = result.nth(1, NIL).toString();
                String filePath = result.nth(2, NIL).toString();
                boolean directoryListing = result.nth(3, NIL).toString()
                        .equals("ls");

                return serveFile(filePath, header, new File(rootDirectory),
                        directoryListing);
            }
            result = result.cdr();
            String mime = "text/html";
            Exp responseHeaders = NIL;
            if (result.car() instanceof StrinG) {
                mime = result.car().toString();
            } else {
                responseHeaders = result.car();
            }
            Exp tmp = responseHeaders;
            while (tmp != NIL) {
                if (tmp.car().car().toString().equals("Content-Type")) {
                    mime = tmp.car().cdr().toString();
                }
                tmp = tmp.cdr();
            }
            if (mime.equals("text/html")) {
                formatter = new HTMLFormatter(output);
            } else if ( mime.equals("application/json")) {
                formatter = new JSONFormatter(output);
            }
            else {
                formatter = new IndentedFormatter(output, 2);
            }
            result = result.cdr().car();
            result.acceptVisitor(formatter);
            output.flush();
            NanoResponse response = new NanoResponse(status, mime,
                    new ByteArrayInputStream(buffer.toByteArray()));
            Exp tmph = responseHeaders;
            while (tmph != NIL) {
                response.addHeader(tmph.car().car().toString(), tmph.car()
                        .cdr().toString());
                tmph = tmph.cdr();
            }
            return response;

        } catch (GenyrisException ey) {
            System.out.println("*** Error: " + ey.getMessage());
            return new NanoResponse(HTTP_OK, "text/plain", "*** Error: "
                    + ey.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new NanoResponse();

    }

    private void interpreterSetup() throws GenyrisException {
        interpreter = new Interpreter();
        interpreter.init(false);
        Symbol ARGS = interpreter.internEscaped(Constants.GENYRIS + "system#" + Constants.ARGS);
        NIL = interpreter.NIL;
        interpreter.getGlobalEnv().defineVariable(ARGS,
                makeListOfArray(NIL, argv));

        Writer output = new PrintWriter(System.out);
        HttpRequestClazz = (Dictionary) interpreter.lookupGlobalFromString("HttpRequest");
        AlistClazz = (Dictionary) interpreter.lookupGlobalFromString("Alist");
        SourceLoader.loadScriptFromFile(interpreter.getGlobalEnv(), interpreter
                .getSymbolTable(), filename, output);

    }
}
