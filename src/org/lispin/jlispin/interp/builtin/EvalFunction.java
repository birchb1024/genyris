package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.LispinException;

public class EvalFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length > 1)
			throw new LispinException("Too many arguments to eval: " + arguments.length);
		return Evaluator.eval(envForBindOperations, arguments[0]);
	}

}
