package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class ConsFunction extends ApplicableFunction {

	public ConsFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
		if( arguments.length < 2)
			throw new GenyrisException("Too few arguments to cons: " + arguments.length);
		return new Lcons(arguments[0], arguments[1]);
	}

}
