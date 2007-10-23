package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class EqFunction extends ApplicableFunction {

	public EqFunction(Interpreter interp) {
		super(interp);
	}


	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
		if( arguments.length != 2)
			throw new GenyrisException("Too few arguments to EqualsFunction: " + arguments.length);
        return arguments[0] == arguments[1] ? TRUE : NIL ;
	}

}
