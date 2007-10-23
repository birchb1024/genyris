package org.lispin.jlispin.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.ClassicFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class LambdaFunction extends ApplicableFunction {

	public LambdaFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws LispinException {

		Exp expression = arrayToList(arguments); // TODO - inefficient
		expression = new Lcons(_lambda, expression);
		return new EagerProcedure(env, expression, new ClassicFunction(_interp));

	}

	public Object getJavaValue() {
		return "<the lambda builtin function>";
	}

}
