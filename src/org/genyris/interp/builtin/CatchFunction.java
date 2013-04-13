// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class CatchFunction extends ApplicableFunction {

    public CatchFunction(Interpreter interp) {
    	super(interp, "catch", false);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        Exp retval = NIL;
        this.checkMinArguments(arguments, 2);
        if( !(arguments[0] instanceof Symbol) ) {
            throw new GenyrisException(this.getName() + " was expecting a Symbol first, but got " + arguments[0].toString());
        }
        Symbol errorVar = (Symbol)arguments[0];
    	env.defineVariable(errorVar, NIL);
        Exp body = arrayToList(arguments).cdr();
        try {
        	retval = body.evalSequence(env);
        } catch (GenyrisException e) {
        	env.setVariableValue(errorVar, e.getData());
        }       
        return retval;
    }
}
