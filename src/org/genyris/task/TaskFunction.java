package org.genyris.task;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class TaskFunction extends ApplicableFunction {

    public TaskFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedure(SleepFunction.class);
        interpreter.bindGlobalProcedure(SpawnFunction.class);
        interpreter.bindGlobalProcedure(KillTaskFunction.class);
        interpreter.bindGlobalProcedure(SpawnHTTPDFunction.class);
    }


}