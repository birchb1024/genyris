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

public class NotFunction extends ApplicableFunction {

	public static String getStaticName() {return "not";};
	public static boolean isEager() {return true;};
	
    public NotFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
        if (arguments.length < 1)
            throw new GenyrisException("Too few arguments to not: " + arguments.length);
        return (arguments[0] == NIL) ? TRUE : NIL;

    }

}
