package org.genyris.web;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;

public abstract class HTTPclientFunction extends ApplicableFunction {
    
    String charset = "UTF-8";

    public HTTPclientFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }

    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException;

//    protected Exp getResponseHeadersAsList(URLConnection conn) throws GenyrisException {
//        //
//        //  Go into an HTTPresponse and retrieve headers as a nested list.
//        //
//        Exp headerList = NIL;
//        int headerIndex = 1;
//        String headerName;
//        while( (headerName = conn.getHeaderFieldKey(headerIndex)) != null ) {
//            StrinG headerValue = new StrinG(conn.getHeaderField(headerIndex));
//            Exp thisHeader = Pair.cons(new StrinG(headerName), headerValue);
//            headerList = Pair.cons(thisHeader, headerList);
//            headerIndex += 1;
//        }
//        return Pair.reverse(headerList, NIL);
//    }

    protected Exp processResponse(String URI, CloseableHttpResponse response) throws IOException {
        try {
            HttpEntity entity = response.getEntity();
            entity = new BufferedHttpEntity(entity);
            Header[] responseHeaders = response.getAllHeaders();
            Exp headerList = NIL;
            for (int i = responseHeaders.length - 1; i >= 0; i--) {
                headerList = Pair.cons(Pair.cons(
                        new StrinG(responseHeaders[i].getName()), new StrinG(
                                responseHeaders[i].getValue())), headerList);
            }
            return Pair.cons2(new ReaderStream( new InputStreamReader(entity.getContent()), URI),
                    headerList, NIL);
        } finally {
            response.close();
        }
    }

    protected void addHeadersToRequest(Exp headers, String charset, HttpMessage httpGetOrPost)
            throws AccessException {

        httpGetOrPost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded;charset=" + charset);
        while (headers != NIL) {
            httpGetOrPost.setHeader(headers.car().car().toString(), headers.car().cdr()
                    .toString());
            headers = headers.cdr();
        }
    }

    protected HttpVersion parseProtocol(Exp protocol) {
        String protocolstring = protocol.toString();
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        if ( protocolstring.equals("1.0") ) {   
            httpVersion = HttpVersion.HTTP_1_0;
        }
        return httpVersion;
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPgetFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new HTTPpostFunction(interpreter));
    }

}
