// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class InternFunction extends ApplicableFunction {

    public InternFunction(Interpreter interp) {
    	super(interp, "intern", true);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
		checkArguments(arguments, 1);
        if( arguments[0] instanceof Symbol) {
            return _interp.intern((Symbol)arguments[0]);
        }
        if( arguments[0] instanceof StrinG ||
            arguments[0] instanceof Bignum) {
            return _interp.intern(Symbol.symbolFactory(arguments[0].toString(), false));
        }
        throw new GenyrisException("Unable to intern object "  + arguments[0].toString() + " with type " + arguments[0].getClass().getName());
    }
}
