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
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;

public class ConditionalFunction extends ApplicableFunction {

	public ConditionalFunction(Interpreter interp) {
		super(interp, "cond", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		for (int i = 0; i < arguments.length; i++) {
			if(! (arguments[i] instanceof Lcons)) {
				throw new GenyrisException("Invalid condition: " + arguments[i]);
			}
			Exp condition = Evaluator.eval(env, arguments[i].car()); 
			if (condition != NIL) {
				if (arguments[i].cdr() == NIL) {
					return condition;
				} else {
					if(! (arguments[i] instanceof Lcons)) {
						throw new GenyrisException("Invalid condition: " + arguments[i]);
					}
					return Evaluator.evalSequence(env, arguments[i].cdr()); 
				}
			}
		}
		return NIL;
	}

	public Object getJavaValue() {
		return "<the cond builtin function>";
	}

}
