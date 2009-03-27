package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class BoundFunction extends ApplicableFunction {

    public BoundFunction(Interpreter interp) {
    	super(interp, "bound?", false);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException  {
		checkArguments(arguments, 1);
		Class[] types = {Symbol.class};
		this.checkArgumentTypes(types, arguments);
        try {
            envForBindOperations.lookupVariableValue((Symbol)arguments[0]);
        } catch (UnboundException e) {
            return NIL;
        }
        return TRUE;
    }

}
