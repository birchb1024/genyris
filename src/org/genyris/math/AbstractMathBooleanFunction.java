// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public abstract class AbstractMathBooleanFunction extends ApplicableFunction {

	public AbstractMathBooleanFunction(Interpreter interp, String name) {
		super(interp, name, true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		if (arguments.length != 2) {
            throw new GenyrisException("Not two arguments to math function");
        }
		Class[] types = { Bignum.class, Bignum.class };
		checkArgumentTypes(types, arguments);
		try {
			return mathOperation(arguments[0], arguments[1]);
		} catch (RuntimeException e) {
			throw new GenyrisException(e.getMessage());
		}
	}

	protected abstract Exp mathOperation(Exp a, Exp b);

}
