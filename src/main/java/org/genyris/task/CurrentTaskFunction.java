// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class CurrentTaskFunction extends TaskFunction {

    public CurrentTaskFunction(Interpreter interp) {
        super(interp, "id", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        return getThreadAsDictionary(Thread.currentThread(), envForBindOperations);
    }

}
