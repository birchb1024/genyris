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

public class RemoveTagFunction extends ApplicableFunction {

	public static String getStaticName() {return "remove-tag";};
	public static boolean isEager() {return true;};
	
    public RemoveTagFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        if( arguments.length != 2)
            throw new GenyrisException("Too few arguments to removetag: " + arguments.length);
        Exp object = arguments[0];
        Exp newClass = arguments[1];
        object.removeClass(newClass);
        return NIL;
    }
}
