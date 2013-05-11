// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.MacroFunction;

public class LambdamFunction extends ApplicableFunction {
	
	public LambdamFunction(Interpreter interp) {
		super(interp, Constants.LAMBDAM, false);
     }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {

        checkFormalArgumentSyntax(arguments[0]);
        Exp expression = arrayToList(arguments);
        expression = new Pair(_lambdam, expression);
        return new LazyProcedure(env, expression, new MacroFunction("anonymous lambdam", _interp));

    }

}
