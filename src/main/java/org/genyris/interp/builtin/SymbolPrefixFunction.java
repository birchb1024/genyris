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

public class SymbolPrefixFunction extends ApplicableFunction {

    public SymbolPrefixFunction(Interpreter interp) {
    	super(interp, "symbol-namespace", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
		checkArguments(arguments, 1);
        if(!(arguments[0] instanceof PrefixSymbol)){
            return NIL;
        }
        PrefixSymbol sym = (PrefixSymbol)arguments[0];
        return Pair.cons(new StrinG(sym._abbrev), new StrinG(sym._prefix));
    }
}
