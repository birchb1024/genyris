// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.StandardClass;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class IsFunction extends ApplicableFunction {

    public IsFunction(Interpreter interp) {
        super(interp, "is?", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment environment) throws GenyrisException {
        checkArguments(arguments, 2);
        Class[] types = { Exp.class, StandardClass.class };
        checkArgumentTypes(types, arguments);
        Exp object = arguments[0];
        StandardClass klass = (StandardClass) arguments[1];
		TagFunction.validateObjectInClass(environment, object, klass);
        return object;
    }

}
