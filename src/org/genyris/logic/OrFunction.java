package org.genyris.logic;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class OrFunction extends ApplicableFunction {

	public OrFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws GenyrisException {
		if (arguments.length < 2)
			throw new GenyrisException("Too few arguments to or: " + arguments.length);

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] == TRUE) {
				return TRUE;
			}
			else if (arguments[i] == NIL) {
				continue;
			}
			else {
				throw new GenyrisException("or expects t or nil, not: " + arguments[i]);				
			}
		}
		return NIL;

	}

}
