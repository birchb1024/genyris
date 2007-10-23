package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.MacroFunction;

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
