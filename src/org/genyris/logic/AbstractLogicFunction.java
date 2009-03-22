package org.genyris.logic;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public abstract class AbstractLogicFunction extends ApplicableFunction {

    public AbstractLogicFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }
    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
    throws GenyrisException;

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws GenyrisException {
        interpreter.bindGlobalProcedure(AndFunction.class);
        interpreter.bindGlobalProcedure(NotFunction.class);
        interpreter.bindGlobalProcedure(OrFunction.class);
    }

}