// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;

public class WhileFunction extends ApplicableFunction {

    public WhileFunction(Interpreter interp) {
    	super(interp, "while", false);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        Exp retval = NIL;
        if(arguments.length < 1) {
            throw new GenyrisException("Too few arguments to while.");
        }
        while( Evaluator.eval(env, arguments[0]) != NIL ) {
            for(int i = 1 ; i < arguments.length; i++) {
                retval =  Evaluator.eval(env, arguments[i]); 
            }
        }
        return retval;
    }
    public Object getJavaValue() {
        return "[the while builtin function]";
    }

}
