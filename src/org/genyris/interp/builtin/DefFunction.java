package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.ClassicFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;

public class DefFunction extends ApplicableFunction {

	public DefFunction(Interpreter interp) {
		super(interp);
	}

public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		Exp lambdaExpression = new Lcons(_lambda, arrayToList(arguments).cdr());
		// TODO inefficient
		EagerProcedure fn = new EagerProcedure(envForBindOperations, lambdaExpression, new ClassicFunction(_interp));
		envForBindOperations.defineVariable(arguments[0], fn);
		return fn;
	}

}
