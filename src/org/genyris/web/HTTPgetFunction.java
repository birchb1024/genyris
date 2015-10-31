// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.io.IOException;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class HTTPgetFunction extends HTTPclientFunction {

    public HTTPgetFunction(Interpreter interp) {
        super(interp, Constants.WEB + "get", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 1, 3);
        Class[] types = {StrinG.class};
        checkArgumentTypes(types, arguments);
        String URI = arguments[0].toString();
        Exp headers = (arguments.length >= 2 ? arguments[1] : NIL);
        if( ! (headers == NIL || headers instanceof org.genyris.core.Pair) ) {
            throw new GenyrisException("Headers must be a list.");
        }
        Exp protocol = (arguments.length >= 3 ? arguments[2] : NIL);
        if( ! (protocol == NIL || protocol instanceof org.genyris.core.StrinG) ) {
            throw new GenyrisException("Protocol must be a String.");
        }
        HttpVersion httpVersion = parseProtocol(protocol);

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {

            HttpGet httpGet = new HttpGet(URI);
            httpGet.setProtocolVersion(httpVersion);

            addHeadersToRequest(headers, charset, httpGet);

            CloseableHttpResponse response = httpclient.execute(httpGet);

            return processResponse(URI, response);
        } catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        } catch (java.lang.RuntimeException e) {
            throw new GenyrisException(e.toString());
        }
        finally {
            try {
                httpclient.close();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }

    }
}
