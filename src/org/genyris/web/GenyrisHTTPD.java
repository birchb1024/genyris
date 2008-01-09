package org.genyris.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Properties;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lstring;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class GenyrisHTTPD extends NanoHTTPD {

    Interpreter interpreter;
    Exp NIL;

    public static void main(String[] args) {
        int port = 8080;

        try {
            new GenyrisHTTPD(port);
        }
        catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

    }

    public GenyrisHTTPD(int port) throws IOException {
        myTcpPort = port;
        try {
            interpreter = new Interpreter();
            interpreter.init(true);
            NIL = interpreter.getNil();
            Writer output = new PrintWriter(System.out);
            SourceLoader.loadScriptFromClasspath(interpreter, "org/genyris/load/boot/httpd-serve.lin", output);
        }
        catch (GenyrisException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        final ServerSocket ss = new ServerSocket(myTcpPort);
        try {
            while (true)
                new HTTPSession(ss.accept());
        }
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public Response serve(String uri, String method, Properties header, Properties parms) {
        Exp request = NIL;
        System.out.println(method + " '" + uri + "' ");

        Exp headers = NIL;
        Enumeration e = header.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            headers = new Lcons(new Lcons(new Lstring(value), new Lstring(header.getProperty(value))), headers);
            System.out.println("  HDR: '" + value + "' = '" + header.getProperty(value) + "'");
        }
        headers = new Lcons(interpreter.getSymbolTable().internString("headers"), headers);

        Exp parameters = NIL;
        e = parms.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            parameters = new Lcons(new Lcons(new Lstring(value), new Lstring(parms.getProperty(value))), parameters);
            System.out.println("  PRM: '" + value + "' = '" + parms.getProperty(value) + "'");
        }
        parameters = new Lcons(interpreter.getSymbolTable().internString("parameters"), parameters);

        request = new Lcons(parameters, request);
        request = new Lcons(headers, request);
        request = new Lcons(new Lstring(uri), request);
        request = new Lcons(new Lstring(method), request);

        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Writer output = new PrintWriter(buffer);
            Formatter formatter = new IndentedFormatter(output, 1, interpreter);
            // (httpd-serve request)
            Exp expression = new Lcons(interpreter.getSymbolTable().internString("httpd-serve"),new Lcons(request,NIL));
            {
                try {

                    Exp result = interpreter.evalInGlobalEnvironment(expression);
                    String status = result.car().toString();
                    result = result.cdr();
                    String mime = result.car().toString();
                    result = result.cdr();
                    result.acceptVisitor(formatter);
                    output.flush();

                    return new Response(status, mime, new ByteArrayInputStream(buffer.toByteArray()));

                }
                catch (GenyrisException ey) {
                    System.out.println("*** Error: " + ey.getMessage());
                    return new Response(HTTP_OK, "text/plain", "*** Error: " + ey.getMessage());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return new Response();
        }
    }
}
