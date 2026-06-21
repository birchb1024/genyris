// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.logic;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class OrFunction extends ApplicableFunction {

    public OrFunction(Interpreter interp) {
    	super(interp, "or", false);
     }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
        if (arguments.length < 2)
            throw new GenyrisException("Too few arguments to or: " + arguments.length);
        Exp result = NIL;
        for (int i = 0; i < arguments.length; i++) {
            result = arguments[i].eval(envForBindOperations);
            if (result != NIL) {
                return result;
            } else {
                continue;
            }
        }
        return NIL;

    }

}
