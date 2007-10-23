package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class SetFunction extends ApplicableFunction {

	public SetFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
		if( arguments.length != 2) throw new GenyrisException("Incorrect number of arguments to set.");
		env.setVariableValue(arguments[0], arguments[1]);
		return arguments[1];
	}
}
