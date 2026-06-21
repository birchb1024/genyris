// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class HashCodeFunction extends ApplicableFunction {

    public HashCodeFunction(Interpreter interp) {
        super(interp, "hashCode", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment environment) throws GenyrisException {
        checkArguments(arguments, 1);
        return new Bignum(arguments[0].hashCode());
    }

}
