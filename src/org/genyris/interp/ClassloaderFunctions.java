//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;

public class ClassloaderFunctions extends ApplicableFunction {

    public ClassloaderFunctions(Interpreter interp) {
        super(interp, Constants.PREFIX_SYSTEM + "load-class-by-name", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 1);
        Class[] types = { StrinG.class };
        checkArgumentTypes(types, arguments);
        _interp.loadClassByName(arguments[0].toString());
        return arguments[0];
    }


    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedure(ClassloaderFunctions.class);
    }

}
