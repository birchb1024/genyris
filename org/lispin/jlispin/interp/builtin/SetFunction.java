package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.AbstractClosure;

public class SetFunction extends ApplicableFunction {

	public Exp bindAndExecute(AbstractClosure proc, Exp[] arguments, Environment env) throws LispinException {
		if( arguments.length != 2) throw new LispinException("Incorrect number of arguments to set.");
		env.setVariableValue(arguments[0], arguments[1]);
		return arguments[1];
	}
}
