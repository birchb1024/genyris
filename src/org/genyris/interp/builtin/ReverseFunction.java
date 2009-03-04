// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ReverseFunction extends ApplicableFunction {

	public ReverseFunction(Interpreter interp) {
		super(interp, "reverse", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 1);
		Exp rev_result = NIL;
		Exp s = arguments[0];

		while (s != NIL) {
			rev_result = new Lcons(s.car(), rev_result);
			s = s.cdr();
		}
		return (rev_result);

	}

}
