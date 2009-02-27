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

import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.readerstream.ReaderStream;

public class HTTPgetFunction extends ApplicableFunction {

	public HTTPgetFunction(Interpreter interp, Lsymbol name) {
		super(interp, name);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		if (arguments.length != 1)
			throw new GenyrisException("Wrong number of arguments to "
					+ getName() + arguments.length);
		// TODO: unsafe downcasts
		String URI = (String) arguments[0].getJavaValue();

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

}
