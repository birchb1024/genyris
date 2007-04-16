package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.Procedure;

public class CdrFunction extends ApplicableFunction {

	public Exp apply(Procedure proc, Environment env, Exp[] arguments) throws LispinException {
		if( arguments.length != 1)
			throw new LispinException("Too many or few arguments to car: " + arguments.length);
		return arguments[0].cdr();
	}

}
