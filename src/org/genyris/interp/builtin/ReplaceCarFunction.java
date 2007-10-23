package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class ReplaceCarFunction extends ApplicableFunction {

	public ReplaceCarFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] argument, Environment envForBindOperations) throws GenyrisException {
		if( argument.length != 2)
			throw new GenyrisException("Too many or few arguments to rplaca: " + argument.length);
		return argument[0].setCar(argument[1]);
	}

}
