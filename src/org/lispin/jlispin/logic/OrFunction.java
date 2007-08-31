package org.lispin.jlispin.logic;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class OrFunction extends ApplicableFunction {

	public OrFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		if (arguments.length < 2)
			throw new LispinException("Too few arguments to or: " + arguments.length);

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] == TRUE) {
				return TRUE;
			}
			else if (arguments[i] == NIL) {
				continue;
			}
			else {
				throw new LispinException("or expects t or nil, not: " + arguments[i]);				
			}
		}
		return NIL;

	}

}
