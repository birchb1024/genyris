package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class ReplaceCdrFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] argument, Environment envForBindOperations) throws LispinException {
		if( argument.length != 2)
			throw new LispinException("Too many or few arguments to rplacd: " + argument.length);
		return argument[0].setCdr(argument[1]);
	}

}
