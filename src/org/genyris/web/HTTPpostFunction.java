// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class HTTPpostFunction extends HTTPclientFunction {

    public HTTPpostFunction(Interpreter interp) {
        super(interp, Constants.WEB + "post", true);
    }

    @Override
    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {

        checkArguments(arguments, 1, 4);
        Class[] types = { StrinG.class };
        checkArgumentTypes(types, arguments);
        String URI = arguments[0].toString();
        Exp params = (arguments.length >= 2 ? arguments[1] : NIL);
        Exp headers = (arguments.length >= 3 ? arguments[2] : NIL);
        if( ! (headers == NIL || headers instanceof org.genyris.core.Pair) ) {
            throw new GenyrisException("Headers must be a list.");
        }
        Exp protocol = (arguments.length >= 4 ? arguments[3] : NIL);
        if( ! (protocol == NIL || protocol instanceof org.genyris.core.StrinG) ) {
            throw new GenyrisException("Protocol must be a String.");
        }
        HttpVersion httpVersion = parseProtocol(protocol);
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {

            HttpPost httpPost = new HttpPost(URI);
            httpPost.setProtocolVersion(httpVersion);

            addHeadersToRequest(headers, charset, httpPost);

            // Now add post parameters...
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            while (params != NIL) {
                nvps.add(new BasicNameValuePair(params.car().car().toString(), params
                        .car().cdr().toString()));
                params = params.cdr();
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = httpclient.execute(httpPost);

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
