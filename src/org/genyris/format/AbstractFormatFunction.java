package org.genyris.format;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class AbstractFormatFunction extends ApplicableFunction {

    public AbstractFormatFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedure(PrintFunction.class);
        interpreter.bindGlobalProcedure(WriteFunction.class);
        interpreter.bindGlobalProcedure(DisplayFunction.class);
    }


}