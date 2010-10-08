// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classification;

import org.genyris.core.Exp;
import org.genyris.core.Dictionary;
import org.genyris.core.StandardClass;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class IsInstanceFunction extends ApplicableFunction {

    public IsInstanceFunction(Interpreter interp) {
        super(interp, "is-instance?", true);
    }
    static Class[] types = {Exp.class, Dictionary.class};

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 2);
        checkArgumentTypes(types, arguments);
        StandardClass.assertIsThisObjectAClass(arguments[1]);
        StandardClass cw = (StandardClass)arguments[1];
        if (cw.isInstance(arguments[0]) )
            return envForBindOperations.getSymbolTable().TRUE();
        else
            return envForBindOperations.getNil();
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws GenyrisException {
        interpreter.bindGlobalProcedureInstance(new IsInstanceFunction(interpreter));

    }

}
