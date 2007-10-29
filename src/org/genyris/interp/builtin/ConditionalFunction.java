// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class ConditionalFunction extends ApplicableFunction {


    public ConditionalFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        for(int i= 0; i < arguments.length; i++) {
            Exp condition = Evaluator.eval(env, arguments[i].car()); // TODO check if it exists?
            if( condition != NIL ) {
                return Evaluator.evalSequence(env, arguments[i].cdr()); // TODO check if it exists?
            }
        }
        return NIL;
    }
    public Object getJavaValue() {
        return "<the cond builtin function>";
    }

}
