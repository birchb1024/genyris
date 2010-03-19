// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class SynchronizeFunction extends TaskFunction {

	public SynchronizeFunction(Interpreter interp) {
		super(interp, "synchronized", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment env) throws GenyrisException {
		checkMinArguments(arguments, 1);
		Exp retval = NIL;
		synchronized (arguments[0].eval(env)) {
            for(int i = 1 ; i < arguments.length; i++) {
                retval =  arguments[i].eval(env); 
            }
			return retval;
		}

	}

}