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
import java.net.MalformedURLException;
import java.net.URL;

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

public class HTTPgetFunction extends ApplicableFunction {

    public HTTPgetFunction(Interpreter interp) {
        super(interp, Constants.WEB + "get", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 1);
        Class[] types = {StrinG.class};
        checkArgumentTypes(types, arguments);
        String URI = arguments[0].toString();

        try {
            URL url = new URL(URI);
            BufferedReader in = new BufferedReader(new InputStreamReader(url
                    .openStream()));
            return new ReaderStream((Reader)in);
        } catch (MalformedURLException e1) {
            throw new GenyrisException(e1.getMessage());
        } catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPgetFunction(interpreter));
    }
}
