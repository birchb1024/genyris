package org.genyris.task;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class TaskFunction extends ApplicableFunction {

    public TaskFunction(Interpreter interp, String name, boolean eager) {
        super(interp, Constants.PREFIX_TASK+ name, eager);
    }

    protected Dictionary getThreadAsDictionary( Thread tr, Environment env) throws GenyrisException {
    	Dictionary result = new Dictionary(env);
    	if(result == null) {
    		System.out.println("Dictionary constructor retuned null !!!!!");
    		System.exit(-1);
    	}
    	result.addProperty(env, "state", new StrinG(tr.getState().toString()));
     	result.addProperty(env, "name", new StrinG(tr.getName()));
     	result.addProperty(env, "id", new Bignum(tr.getId()));
        return result;
       }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new SleepFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SpawnFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new KillTaskFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new SpawnHTTPDFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ListTaskFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new CurrentTaskFunction(interpreter));               
        interpreter.bindGlobalProcedureInstance(new SynchronizeFunction(interpreter));               
    }


}