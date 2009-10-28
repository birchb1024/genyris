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

public class UseFunction extends ApplicableFunction {

	public UseFunction(Interpreter interp) {
		super(interp, "use", false);
	}

	public Exp bindAndExecute(Closure ignored, Exp[] arguments, Environment env)
			throws GenyrisException {
		this.checkMinArguments(arguments, 2);
		Exp object = arguments[0].eval(env);
		Environment newEnv = object.makeEnvironment(env);
		Exp retval = NIL;
        for(int i = 1 ; i < arguments.length; i++) {
            retval =  arguments[i].eval(newEnv); 
        }
		return retval;
	}

}
