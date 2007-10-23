package org.lispin.jlispin.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.MacroFunction;

public class DefMacroFunction extends ApplicableFunction {

	public DefMacroFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		Exp lambdaExpression = new Lcons(_lambdam, arrayToList(arguments).cdr());
		// TODO inefficient
		LazyProcedure fn = new LazyProcedure(envForBindOperations, lambdaExpression, new MacroFunction(_interp));
		envForBindOperations.defineVariable(arguments[0], fn);
		return fn;
	}

}
