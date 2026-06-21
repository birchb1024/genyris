// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.TailCall;

public class TailCallFunction extends BuiltinFunction {

    public TailCallFunction(Interpreter interp) {
        super(interp, "tailcall", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        if(arguments.length == 0) {
            throw new GenyrisException("tailcall expected closure as first argument but got nothing.");
        }
        if(!(arguments[0] instanceof Closure)) {
            throw new GenyrisException("tailcall expected closure as first argument but got: " + arguments[0]);
        }
        Exp[] newargs = new Exp[arguments.length -1];
        System.arraycopy(arguments, 1, newargs, 0, newargs.length);
        return new TailCall((Closure)arguments[0], newargs);
    }
}
