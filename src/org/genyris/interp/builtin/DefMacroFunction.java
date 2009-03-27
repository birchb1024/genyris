// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.MacroFunction;

public class DefMacroFunction extends ApplicableFunction {

	public DefMacroFunction(Interpreter interp) {
		super(interp, "defmacro", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkMinArguments(arguments, 1);
		Class[] types = {Symbol.class};
		checkArgumentTypes(types, arguments);
		Exp lambdaExpression = new Pair(_lambdam, arrayToList(arguments).cdr());
		LazyProcedure fn = new LazyProcedure(envForBindOperations,
				lambdaExpression, new MacroFunction(arguments[0].toString(), _interp));
		envForBindOperations.defineVariable((Symbol)arguments[0], fn);
		return fn;
	}

}
