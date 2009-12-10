// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class RemoveTagFunction extends ApplicableFunction {

    public RemoveTagFunction(Interpreter interp) {
    	super(interp, "remove-tag", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
		checkArguments(arguments, 2);
        Class[] types = {Dictionary.class, Exp.class};
        checkArgumentTypes(types, arguments);

        Exp object = arguments[1];
        Exp newClass = arguments[0];
        object.removeClass(newClass);
        return object;
    }
}
