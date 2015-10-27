// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;

public class HTTPgetFunction extends ApplicableFunction {

    public HTTPgetFunction(Interpreter interp) {
        super(interp, Constants.WEB + "get", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 1, 2);
        Class[] types = {StrinG.class};
        checkArgumentTypes(types, arguments);
        String URI = arguments[0].toString();
        Exp headers = (arguments.length == 2 ? arguments[1] : NIL);

        try {
            URL url = new URL(URI);
            URLConnection conn = url.openConnection();
            if(!(conn instanceof HttpURLConnection)) {
                throw new GenyrisException("Not an HttpURLConnection: " + conn);
            }
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            while (headers != NIL) {
            	conn.addRequestProperty(headers.car().car().toString(), headers.car().cdr().toString());
            	headers = headers.cdr();
            }
        	httpConn.connect();
        	if (httpConn.getResponseCode() != 200) {
            	throw new GenyrisException("Server returned non 200 Response Code: " + Integer.toString(httpConn.getResponseCode()));
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Map responseHeaders = conn.getHeaderFields(); // TODO DRY see Post
            Exp headerList = NIL;
            Set<Map.Entry<String, List<String>>> entrySet = responseHeaders.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                Exp headerName = entry.getKey() == null ? NIL : new StrinG(entry.getKey());
                System.out.println(headerName.toString());
                List<String> headerValues = entry.getValue();
                Exp valueList = NIL;
                for (String value : headerValues) {
                    System.out.println(value);
                    valueList = Pair.cons(new StrinG(value), valueList);
                }
                Exp thisHeader = Pair.cons(headerName, Pair.reverse(valueList, NIL));
                headerList = Pair.cons(thisHeader, headerList); 
            }
            return Pair.cons2(new ReaderStream((Reader) in, URI), Pair.reverse(headerList, NIL), NIL);

        } catch (MalformedURLException e1) {
            throw new GenyrisException(e1.getMessage());
        } catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        } catch (java.lang.RuntimeException e) {
        	if(e.getMessage().equals("java.lang.IllegalArgumentException: protocol = http host = null"))	
        		throw new GenyrisException("Proably got a 302 redirection to a bad URL?" + e.getMessage());
        	else
        		throw e;
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPgetFunction(interpreter));
    }
}
