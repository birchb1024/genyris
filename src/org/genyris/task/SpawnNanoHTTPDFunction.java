// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import java.io.IOException;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.web.NanoHTTPD;

public class SpawnNanoHTTPDFunction extends ApplicableFunction {

	public SpawnNanoHTTPDFunction(Interpreter interp) {
		super(interp, Constants.WEB + "serve-static", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 2);
    	Class[] types = {Bignum.class, StrinG.class};
    	checkArgumentTypes(types, arguments);
		int port = ((Bignum) arguments[0]).bigDecimalValue().intValue();
		String rootpath = arguments[1].toString();
		try {
			NanoHTTPD httpd1 = new NanoHTTPD(port, rootpath);
			Thread t = httpd1.run();
			return new Bignum(t.getId());
		} catch (IOException e) {
			return new StrinG(e.getMessage());
		}

	}

}
