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

import org.genyris.core.*;
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

        String URI = getArg(arguments, 0, StrinG.class, true).toString();
        Exp headers = getArg(arguments, 1, Exp.class);
        Exp protocol = getArg(arguments, 2, Exp.class);
        Exp options = getArg(arguments, 3, Symbol.class);

        CloseableHttpClient httpclient = getCloseableHttpClient(options);
        HttpVersion httpVersion = parseProtocol(protocol);

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
