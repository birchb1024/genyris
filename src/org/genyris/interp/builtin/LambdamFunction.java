package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.LispinException;
import org.genyris.interp.MacroFunction;

public class LambdamFunction extends ApplicableFunction {

	public LambdamFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws LispinException {

		// TODO - inefficient
		Exp expression = arrayToList(arguments);
		expression = new Lcons(_lambdam, expression);
		return new LazyProcedure(env, expression, new MacroFunction(_interp));

	}

	public Object getJavaValue() {
		return "<the lambdam builtin function>";
	}

}
