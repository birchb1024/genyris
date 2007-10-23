package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class EqualsFunction extends ApplicableFunction {

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
