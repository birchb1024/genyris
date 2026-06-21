// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

import java.util.Arrays;

public class LengthFunction extends ApplicableFunction {

	public LengthFunction(Interpreter interp) {
		super(interp, "length", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 1);
		Exp s = arguments[0];
		if(s == _interp.NIL) {
			return new Bignum(0); // Because an empty list is NIL.
		}
		Class[][] allowed = {{StrinG.class, Pair.class}};
		checkArgumentTypes(allowed, arguments);
		return (new Bignum(s.length(NIL)));
	}
}
