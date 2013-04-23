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
import org.genyris.interp.Interpreter;

public class BackTraceFunction extends ApplicableFunction {

    public BackTraceFunction(Interpreter interp) {
    	super(interp, "http://www.genyris.org/lang/system#backtrace", false);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        
        Exp bt = _interp.getDebugBackTraceAsList();
        _interp.resetDebugBackTrace();
        return bt;
    }
}
