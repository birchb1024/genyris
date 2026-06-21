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

public class SymbolValueFunction extends ApplicableFunction {

    public SymbolValueFunction(Interpreter interp) {
    	super(interp, "symbol-value", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
		checkArguments(arguments, 1);
		Class[] types = {Symbol.class};
    	checkArgumentTypes(types, arguments);
        return envForBindOperations.lookupVariableValue((Symbol)arguments[0]);
    }
}
