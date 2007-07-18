package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class CdrFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length != 1)
			throw new LispinException("Too many or few arguments to car: " + arguments.length);
		return arguments[0].cdr();
	}

}
