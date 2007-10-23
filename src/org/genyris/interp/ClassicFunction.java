package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.AccessException;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;

public class ClassicFunction extends ApplicableFunction {
	private Exp REST;
	public ClassicFunction(Interpreter interp) {
		super(interp);
		REST = interp.getSymbolTable().internString(Constants.REST);
	}

	public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment envForBindOperations) throws LispinException  { 

		AbstractClosure proc = (AbstractClosure)closure; // TODO run time validation
		Map bindings = new HashMap();
		if(arguments.length < proc.getNumberOfRequiredArguments()) {
			throw new LispinException("Too few arguments supplied to proc: " + proc.getName());
		}
		for( int i=0 ; i< arguments.length ; i++ ) {
			Exp formal = proc.getArgumentOrNIL(i);
			if( formal == REST ) {
				Lcons actuals = assembleListFromRemainingArgs(arguments, i);
				formal = proc.getLastArgumentOrNIL(i+1);
				if( formal != NIL ) {
					bindings.put(formal, actuals);
				}
				break;
			}
			else if( formal != NIL ) {
				bindings.put(formal, arguments[i]);
			}
		}
		// Use the procedure's frame to get lexical scope
		// and the dynamic environment for the object stuff.
	    Environment newEnv = new SpecialEnvironment(proc.getEnv(), bindings, envForBindOperations); 	       

		return Evaluator.evalSequence(newEnv, proc.getBody());
	}

	private Lcons assembleListFromRemainingArgs(Exp[] arguments, int i) throws LispinException, AccessException {
		Lcons actuals = new Lcons(arguments[i], NIL);
		Lcons tail = actuals;
		for(int j=i+1; j< arguments.length ; j++ ) {
			Lcons newTail = new Lcons(arguments[j], NIL);
			tail.setCdr(newTail);
			tail = newTail;
		}
		return actuals;
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
