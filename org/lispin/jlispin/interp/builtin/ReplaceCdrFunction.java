package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.Procedure;

public class ReplaceCdrFunction extends ApplicableFunction {

	public Exp apply(Procedure proc, Environment env, Exp[] argument) throws LispinException {
		if( argument.length != 2)
			throw new LispinException("Too many or few arguments to rplacd: " + argument.length);
		return argument[0].setCdr(argument[1]);
	}

}
