package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;

public class QuoteFunction extends ApplicableFunction {

	public QuoteFunction(Interpreter interp) {
		super(interp);
	}


	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length > 1)
			throw new LispinException("Too many arguments to quote: " + arguments.length);
		return arguments[0];
	}

}
