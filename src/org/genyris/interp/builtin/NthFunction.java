// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class NthFunction extends ApplicableFunction {

    public NthFunction(Interpreter interp) {
    	super(interp, "nth", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 2);
        Class[] types = {Bignum.class, Exp.class, };
        checkArgumentTypes(types, arguments);
        return arguments[1].nth(((Bignum)arguments[0]).bigDecimalValue().intValue(),NIL);
    }

}
