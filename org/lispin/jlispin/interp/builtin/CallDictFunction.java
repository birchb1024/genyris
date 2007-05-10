package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.Procedure;

public class CallDictFunction extends ApplicableFunction {

	public Exp bindAndExecute(Procedure proc, Exp[] arguments, Environment env) throws LispinException {

		return env.evalSequence(arguments[0]); // TODO check if it exists?

	}
	public Object getJavaValue() {
		return "<the cond builtin function>";
	}

}
