// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class HTTPpostFunction extends HTTPclientFunction {

    public HTTPpostFunction(Interpreter interp) {
        super(interp, Constants.WEB + "post", true);
    }

    public static boolean isURI(StrinG X) {
        try {
            URI uri = new URI(X.toString());
            return uri.isAbsolute();
        }
        catch (URISyntaxException e) { }
        return false;
    }

    @Override
    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {

        StrinG URI = (StrinG)getArg(arguments, 0, StrinG.class, true);
        Exp params = getArg(arguments, 1, Exp.class);
        Exp headers = getArg(arguments, 2, Exp.class);
        Exp protocol = getArg(arguments, 3, Exp.class);
        Exp options = getArg(arguments, 4, Symbol.class);

        CloseableHttpClient httpclient;
        HttpVersion httpVersion;

        try {
            httpclient = getCloseableHttpClient(options);
            httpVersion = parseProtocol(protocol);
        } catch (Exception e) {
            throw new GenyrisException(e.toString());
        }

        try {
            if(!isURI(URI)) {
                throw new GenyrisException("post URI is not valid: " + URI.toString());
            }
            HttpPost httpPost = new HttpPost(URI.toString());
            httpPost.setProtocolVersion(httpVersion);

            addHeadersToRequest(headers, charset, httpPost);

            // Now add post parameters...
            if(params instanceof Pair) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                while (params != NIL) {
                    Exp item = params.car();
                    if(!(item instanceof Pair)){
                        throw new GenyrisException("post parameters are not an Assoc: " + params);
                    }
                    nvps.add(new BasicNameValuePair(
                            item.car().toString(),
                            item.cdr().toString()));
                    params = params.cdr();
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            } else {
                httpPost.setEntity(new StringEntity(params.toString()));
            }
            CloseableHttpResponse response = httpclient.execute(httpPost);
            return processResponse(URI.toString(), response);
        } catch (Exception e) {
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
