package org.lispin.jlispin.logic;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;

public class AndFunction extends ApplicableFunction {

	public AndFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		if (arguments.length < 2)
			throw new LispinException("Too few arguments to and: " + arguments.length);

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] == NIL) {
                return NIL;
			}
		}
		return TRUE;

	}

}
