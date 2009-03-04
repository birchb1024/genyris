package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class BoundFunction extends ApplicableFunction {


	public static String getStaticName() {return "bound?";};
	public static boolean isEager() {return false;};
	
    public BoundFunction(Interpreter interp) {
    	super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException  {
        if( arguments.length > 1)
            throw new GenyrisException("Too many arguments to bound?: " + arguments.length);
        try {
            envForBindOperations.lookupVariableValue(arguments[0]);
        } catch (UnboundException e) {
            return NIL;
        }
        return TRUE;
    }

}
