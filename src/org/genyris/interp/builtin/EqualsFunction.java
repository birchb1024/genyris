// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class EqualsFunction extends ApplicableFunction {

	public static String getStaticName() {return "equal?";};
	public static boolean isEager() {return true;};
	
    public EqualsFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length != 2)
            throw new GenyrisException("Too few arguments to EqualsFunction: " + arguments.length);
        if( arguments[0].deepEquals(arguments[1]) )
            return TRUE;
        else
            return NIL;
    }

}
