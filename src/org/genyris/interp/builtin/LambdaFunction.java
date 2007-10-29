// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.ClassicFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class LambdaFunction extends ApplicableFunction {

    public LambdaFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {

        Exp expression = arrayToList(arguments); // TODO - inefficient
        expression = new Lcons(_lambda, expression);
        return new EagerProcedure(env, expression, new ClassicFunction(_interp));

    }

    public Object getJavaValue() {
        return "<the lambda builtin function>";
    }

}
