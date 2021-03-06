// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ConditionalFunction extends ApplicableFunction {

	public ConditionalFunction(Interpreter interp) {
		super(interp, "cond", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		for (int i = 0; i < arguments.length; i++) {
			if(! (arguments[i] instanceof Pair)) {
				throw new GenyrisException("Invalid condition: " + arguments[i]);
			}
			Exp condition = arguments[i].car().eval(env); 
			if (condition != NIL) {
				if (arguments[i].cdr() == NIL) {
					return condition;
				} else {
					if(! (arguments[i] instanceof Pair)) {
						throw new GenyrisException("Invalid condition: " + arguments[i]);
					}
					return arguments[i].cdr().evalSequence(env); 
				}
			}
		}
		return NIL;
	}

}
