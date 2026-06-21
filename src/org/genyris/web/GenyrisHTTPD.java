package org.genyris.web;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.format.*;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

import com.sun.net.httpserver.HttpExchange;

import static org.genyris.interp.ClassicReadEvalPrintLoop.getContainingDirectoryPath;

public class GenyrisHTTPD extends GenyrisHttpServer {

    private Interpreter interpreter;
    private final String filename;
    private final Exp[] _argv;

    Symbol NIL;
    Dictionary HttpRequestClazz, AlistClazz;

    public GenyrisHTTPD(int port, String filename, Exp[] argv) {
        myTcpPort     = port;
        this.filename = filename;
        this._argv    = argv;
    }

    @Override
    public Thread run() throws IOException {
        try {
            interpreterSetup();
        } catch (GenyrisException e) {
            throw new IOException("GenyrisHTTPD interpreter setup failed: " + e.getMessage(), e);
        }
        Thread t = super.run();
        t.setName(getClass().getName() + " " + myTcpPort + " " + filename);
        return t;
    }

    @Override
    public synchronized HttpResponse serve(long sessionNumber,
                                           String clientIP,
                                           int clientPort,
                                           String uri,
                                           String method,
                                           Properties header,
                                           Properties parms) {
        // Build headers alist
        Exp headers = NIL;
        Enumeration<?> e = header.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            headers = new Pair(new Pair(new StrinG(key), new StrinG(header.getProperty(key))), headers);
        }
        headers.addClass(AlistClazz);

        // Build parameters alist
        Exp parameters = NIL;
        e = parms.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            parameters = new Pair(new Pair(new StrinG(key), new StrinG(parms.getProperty(key))), parameters);
        }
        parameters.addClass(AlistClazz);

        // Assemble request Exp — same structure as before
        Exp request = NIL;
        request = new Pair(new Bignum(sessionNumber), request);
        request = new Pair(new Bignum(clientPort), request);
        request = new Pair(new Pair(new StrinG(clientIP), NIL), request);
        request = new Pair(parameters, request);
        request = new Pair(headers, request);
        request = new Pair(new StrinG(uri), request);
        request = new Pair(new StrinG(method), request);
        request.addClass(HttpRequestClazz);

        Exp expression = new Pair(interpreter.intern("httpd-serve"), new Pair(request, NIL));
        try {
            Exp result = interpreter.evalInGlobalEnvironment(expression);
            String status = result.nth(0, NIL).toString();

            if (status.equals("SERVE-FILE")) {
                String rootDirectory = result.nth(1, NIL).toString();
                pendingFileRoot.set(rootDirectory);
                return null; // signals base class to call serveStatic()
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
                if (tmp.car().car().toString().equals("Content-Type"))
                    mime = tmp.car().cdr().toString();
                tmp = tmp.cdr();
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Writer output = new PrintWriter(buffer);
            Formatter formatter;
            if (mime.equals("text/html"))             formatter = new HTMLFormatter(output);
            else if (mime.equals("application/json")) formatter = new JSONFormatter(output);
            else                                      formatter = new IndentedFormatter(output, 2);

            result.cdr().car().acceptVisitor(formatter);
            output.flush();

            HttpResponse response = new HttpResponse(status, mime,
                    new ByteArrayInputStream(buffer.toByteArray()));
            Exp tmph = responseHeaders;
            while (tmph != NIL) {
                response.addHeader(tmph.car().car().toString(), tmph.car().cdr().toString());
                tmph = tmph.cdr();
            }
            return response;

        } catch (GenyrisException ey) {
            System.out.println("*** Error: " + ey.getMessage());
            return new HttpResponse(HTTP_OK, MIME_PLAINTEXT, "*** Error: " + ey.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new HttpResponse(HTTP_INTERNALERROR, MIME_PLAINTEXT, ex.getMessage());
        }
    }

    // ThreadLocal carries the dynamic root from serve() into serveStatic(),
    // which runs on the same request thread.
    private static final ThreadLocal<String> pendingFileRoot = new ThreadLocal<>();

    @Override
    protected void serveStatic(HttpExchange exchange, String ignored) throws IOException {
        String root = pendingFileRoot.get();
        pendingFileRoot.remove();
        super.serveStatic(exchange, root);
    }

    private static Exp makeListOfArray(Symbol NIL, Exp[] args) {
        Exp arglist = NIL;
        for (int i = args.length - 1; i > 0; i--)
            arglist = new Pair(args[i], arglist);
        return arglist;
    }

    private void interpreterSetup() throws GenyrisException {
        interpreter = new Interpreter();
        interpreter.init(false, getContainingDirectoryPath(filename));
        Symbol argv = interpreter.intern(
                new PrefixSymbol(Constants.GENYRIS + "system#", Constants.ARGV, "sys"));
        NIL = interpreter.NIL;
        interpreter.getGlobalEnv().defineVariable(argv, makeListOfArray(NIL, _argv));
        Writer output = new PrintWriter(System.out);
        HttpRequestClazz = (Dictionary) interpreter.lookupGlobalFromString("HttpRequest");
        AlistClazz       = (Dictionary) interpreter.lookupGlobalFromString("Alist");
        SourceLoader.loadScriptFromFile(interpreter.getGlobalEnv(),
                interpreter.getSymbolTable(), filename, output);
    }
}
