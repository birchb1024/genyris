package org.genyris.format;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Interpreter;

public abstract class AbstractFormatFunction extends ApplicableFunction {

    public AbstractFormatFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws GenyrisException {
        interpreter.bindGlobalProcedureInstance(new PrintFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new WriteFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DisplayFunction(interpreter));
    }


}