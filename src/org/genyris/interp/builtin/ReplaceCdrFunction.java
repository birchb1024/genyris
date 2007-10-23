package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;

public class ReplaceCdrFunction extends ApplicableFunction {

	public ReplaceCdrFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] argument, Environment envForBindOperations) throws LispinException {
		if( argument.length != 2)
			throw new LispinException("Too many or few arguments to rplacd: " + argument.length);
		return argument[0].setCdr(argument[1]);
	}

}
