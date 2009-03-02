// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.MacroFunction;

public class DefMacroFunction extends ApplicableFunction {

	public static String getStaticName() {
		return "defmacro";
	};

	public static boolean isEager() {
		return false;
	};

	public DefMacroFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		Exp lambdaExpression = new Lcons(_lambdam, arrayToList(arguments).cdr());
		// TODO inefficient
		LazyProcedure fn = new LazyProcedure(envForBindOperations,
				lambdaExpression, new MacroFunction(arguments[0], _interp));
		envForBindOperations.defineVariable(arguments[0], fn);
		return fn;
	}

}
