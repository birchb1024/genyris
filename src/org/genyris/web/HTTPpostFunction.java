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
import org.genyris.core.Symbol;
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

        String URI = getArg(arguments, 0, StrinG.class, true).toString();
        Exp params = getArg(arguments, 1, Exp.class);
        Exp headers = getArg(arguments, 2, Exp.class);
        Exp protocol = getArg(arguments, 3, Exp.class);
        Exp options = getArg(arguments, 4, Symbol.class);

        CloseableHttpClient httpclient = getCloseableHttpClient(options);
        HttpVersion httpVersion = parseProtocol(protocol);

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
