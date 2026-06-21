// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.logic;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class AndFunction extends AbstractLogicFunction {

    public AndFunction(Interpreter interp) {
        super(interp, "and", false);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
        if (arguments.length < 2)
            throw new GenyrisException("Too few arguments to and: " + arguments.length);
        Exp result = NIL;
        for (int i = 0; i < arguments.length; i++) {
            result = arguments[i].eval(envForBindOperations);
            if (result == NIL) {
                return NIL;
            }
        }
        return result;

    }

}
