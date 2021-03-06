// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ApplyFunction extends BuiltinFunction {

    public ApplyFunction(Interpreter interp) {
        super(interp, "apply", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 2);
        Closure functionToApply = arguments[0];
        Exp[] args = makeArray(arguments[1], NIL);
        return functionToApply.applyFunction(envForBindOperations, args);
    }

    private Exp[] makeArray(Exp list, Symbol NIL) throws AccessException {
        // TODO - Refactor for efficiency?
        int i = 0;
        Exp[] result = new Exp[list.length(NIL)];
        while (list != NIL) {
            result[i] = list.car();
            list = list.cdr();
            i++;
        }
        return result;
    }
}
