// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;

public class ObjectFunction extends ApplicableFunction {
	public static String getStaticName() {
		return "dict";
	};

	public static boolean isEager() {
		return false;
	};

	public ObjectFunction(Interpreter interp) {
		super(interp, getStaticName());
	}

	public Exp bindAndExecute(Closure ignored, Exp[] arguments, Environment env)
			throws GenyrisException {
		Lobject dict = new Lobject(env);
		for (int i = 0; i < arguments.length; i++) {
			if (!arguments[i].listp())
				throw new GenyrisException("argument to dict not a list");
			dict.defineVariable(arguments[i].car(), Evaluator.eval(env,
					arguments[i].cdr()));
		}
		return dict;
	}
}
