// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.datetime;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class AbstractDateTimeFunction extends ApplicableFunction {


    public AbstractDateTimeFunction(Interpreter interp,
            String name) {
        super(interp, name, true);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new FormatDateFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DetailedDateTimeFunction(interpreter));

    }

}