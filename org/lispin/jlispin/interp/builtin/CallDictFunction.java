package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.LispinException;

public class CallDictFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {

		return Evaluator.evalSequence(env, arguments[0]); 

	}
	public Object getJavaValue() {
		return "<the cond builtin function>";
	}

}
