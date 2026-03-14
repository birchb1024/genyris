// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class SlashFunction extends ApplicableFunction {

	public SlashFunction(Interpreter interp) {
		super(interp, "slash", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
        Exp lhs = arguments[0].eval(env);
        Exp rhs = arguments[1];
        if (lhs instanceof Dictionary) {
            if (rhs instanceof SimpleSymbol) {
                DynamicSymbol var = new DynamicSymbol((SimpleSymbol) rhs);
                Dictionary dict = (Dictionary) lhs;
                return dict.lookupDynamicVariableValue(var);
            }
            else {
                throw new GenyrisException("Unimplemented: slash " +  _interp.arrayToList(arguments));
            }
        }
		throw new GenyrisException("Unimplemented: slash " + _interp.arrayToList(arguments));
	}
}
