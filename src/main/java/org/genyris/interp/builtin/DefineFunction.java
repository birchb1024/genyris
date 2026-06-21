// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class DefineFunction extends ApplicableFunction {

    public DefineFunction(Interpreter interp) {
    	super(interp, "defvar", true);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
    	checkArguments(arguments, 2);
		Class[] types = {Symbol.class};
		checkArgumentTypes(types, arguments);
    	env.defineVariable((Symbol)arguments[0], arguments[1]);

        return arguments[1];
    }
}
