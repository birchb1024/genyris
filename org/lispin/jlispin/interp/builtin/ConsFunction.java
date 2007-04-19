package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.Procedure;

public class ConsFunction extends ApplicableFunction {

	public Exp bindAndExecute(Procedure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length < 2)
			throw new LispinException("Too few arguments to cons: " + arguments.length);
		return new Lcons(arguments[0], arguments[1]);
	}

}
