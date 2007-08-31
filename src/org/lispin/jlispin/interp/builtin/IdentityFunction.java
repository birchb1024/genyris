package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class IdentityFunction extends ApplicableFunction {

	public IdentityFunction(Interpreter interp) {
		super(interp);
	}
	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length != 1)
			throw new LispinException("Wrong number of arguments to identity function: " + arguments.length);
		return arguments[0];
	}

}
