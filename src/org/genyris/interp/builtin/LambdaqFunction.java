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
import org.genyris.interp.ClassicFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;

public class LambdaqFunction extends ApplicableFunction {

    public LambdaqFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        // TODO Optimise - repack of args inefficient
        Exp expression = arrayToList(arguments);
        expression = new Lcons(_lambdaq, expression);
        return new LazyProcedure(env, expression, new ClassicFunction(_interp));
    }

    public Object getJavaValue() {
        return "<the lambdaq builtin function>";
    }

}
