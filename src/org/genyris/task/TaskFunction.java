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
        interpreter.bindGlobalProcedureInstance(new SleepFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SpawnFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new KillTaskFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SpawnHTTPDFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SpawnNanoHTTPDFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ListTaskFunction(interpreter));
               
    }


}