package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.ClassicFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.LispinException;

public class LambdaqFunction extends ApplicableFunction {

	public LambdaqFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws LispinException {
		// TODO Optimise - repack of args inefficient
		Exp expression = arrayToList(arguments);
		expression = new Lcons(_lambdaq, expression);
		return new LazyProcedure(env, expression, new ClassicFunction(_interp));
	}

	public Object getJavaValue() {
		return "<the lambdaq builtin function>";
	}

}
