// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;

public class HTTPpostFunction extends ApplicableFunction {

    public HTTPpostFunction(Interpreter interp) {
        super(interp, Constants.WEB + "post", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        String charset = "UTF-8";

        checkArguments(arguments, 1, 3);
        Class[] types = { StrinG.class };
        checkArgumentTypes(types, arguments);
        String URI = arguments[0].toString();
        Exp params = (arguments.length >= 2 ? arguments[1] : NIL);
        Exp headers = (arguments.length == 3 ? arguments[2] : NIL);

        try {
            URLConnection conn = new URL(URI).openConnection();
            if (!(conn instanceof HttpURLConnection)) {
                throw new GenyrisException("Not an HttpURLConnection: " + conn);
            }
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            conn.setDoOutput(true); // Triggers POST.
            conn.setRequestProperty("Accept-Charset", charset);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=" + charset);
            while (headers != NIL) {
                conn.addRequestProperty(headers.car().car().toString(), headers.car()
                        .cdr().toString());
                headers = headers.cdr();
            }
            String query = "";
            while (params != NIL) {
                query = query.concat(URLEncoder.encode(params.car().car().toString(),
                        charset));
                query = query.concat("=");
                query = query.concat(URLEncoder.encode(params.car().cdr().toString(),
                        charset));
                if (params.cdr() != NIL) {
                    query = query.concat("&");
                }
                params = params.cdr();
            }
            query = query.concat("\r\n");
            
            OutputStream output = conn.getOutputStream();
            output.write(query.getBytes(charset));
            output.flush();
            httpConn.connect();
            if (httpConn.getResponseCode() != 200) {
                throw new GenyrisException("Server returned non 200 Response Code: "
                        + Integer.toString(httpConn.getResponseCode()));
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            return new ReaderStream((Reader) in, URI);

        } catch (MalformedURLException e) {
            throw new GenyrisException(e.getMessage());
        } catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        } catch (java.lang.RuntimeException e) {
            if (e.getMessage().equals(
                    "java.lang.IllegalArgumentException: protocol = http host = null"))
                throw new GenyrisException("Proably got a 302 redirection to a bad URL?"
                        + e.getMessage());
            else
                throw e;
        }
        /*
         * 
         * try { URL url = new URL(URI); URLConnection conn =
         * url.openConnection();
         * 
         * HttpURLConnection httpConn = (HttpURLConnection)conn; while (headers
         * != NIL) { conn.addRequestProperty(headers.car().car().toString(),
         * headers.car().cdr().toString()); headers = headers.cdr(); }
         * httpConn.connect(); if (httpConn.getResponseCode() != 200) { throw
         * new GenyrisException("Server returned non 200 Response Code: " +
         * Integer.toString(httpConn.getResponseCode())); } BufferedReader in =
         * new BufferedReader(new InputStreamReader(conn.getInputStream()));
         * return new ReaderStream((Reader)in, URI); } catch
         * (MalformedURLException e1) { throw new
         * GenyrisException(e1.getMessage()); } catch (IOException e) { throw
         * new GenyrisException(e.getMessage()); } catch
         * (java.lang.RuntimeException e) { if(e.getMessage().equals(
         * "java.lang.IllegalArgumentException: protocol = http host = null"))
         * throw new
         * GenyrisException("Proably got a 302 redirection to a bad URL?" +
         * e.getMessage()); else throw e; }
         */
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPpostFunction(interpreter));
    }
}
