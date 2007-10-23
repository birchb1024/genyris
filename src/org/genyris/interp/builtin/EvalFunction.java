package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class EvalFunction extends ApplicableFunction {

	public EvalFunction(Interpreter interp) {
		super(interp);
	}
	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
		if( arguments.length > 1)
			throw new GenyrisException("Too many arguments to eval: " + arguments.length);
		return Evaluator.eval(envForBindOperations, arguments[0]);
	}

}
