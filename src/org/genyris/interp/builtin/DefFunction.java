package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.ClassicFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

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
