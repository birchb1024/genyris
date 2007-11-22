// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.GenyrisException;
import org.genyris.interp.Interpreter;

public class SymbolValueFunction extends ApplicableFunction {

    public SymbolValueFunction(Interpreter interp) {
        super(interp);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length != 1)
            throw new GenyrisException("symbol-value expects one argument.");
        if(! (arguments[0] instanceof Lsymbol) ) {
            throw new GenyrisException("symbol-value expects a symbol.");
        }
        return envForBindOperations.lookupVariableValue((Lsymbol) arguments[0]);
    }

}
