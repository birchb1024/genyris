package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;

public class ClassicFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment envForBindOperations) throws LispinException  { 

		AbstractClosure proc = (AbstractClosure)closure; // TODO run time validation
		Map bindings = new HashMap();
		if(arguments.length < proc.getNumberOfRequiredArguments()) {
			throw new LispinException("Too few arguments supplied to proc: " + proc.getName());
		}
		for( int i=0 ; i< arguments.length ; i++ ) {
			Exp formal = proc.getArgumentOrNIL(i);
			if( formal == SymbolTable.REST ) {
				Lcons actuals = assembleListFromRemainingArgs(arguments, i);
				formal = proc.getLastArgumentOrNIL(i+1);
				if( formal != SymbolTable.NIL ) {
					bindings.put(formal, actuals);
				}
				break;
			}
			else if( formal != SymbolTable.NIL ) {
				bindings.put(formal, arguments[i]);
			}
		}
		// Use the procedure's frame to get lexical scope
		// and the dynamic environment for the object stuff.
	    Environment newEnv = new SpecialEnvironment(proc.getEnv(), bindings, envForBindOperations); 	       

		return Evaluator.evalSequence(newEnv, proc.getBody());
	}

	private Lcons assembleListFromRemainingArgs(Exp[] arguments, int i) throws LispinException, AccessException {
		Lcons actuals = new Lcons(arguments[i], SymbolTable.NIL);
		Lcons tail = actuals;
		for(int j=i+1; j< arguments.length ; j++ ) {
			Lcons newTail = new Lcons(arguments[j], SymbolTable.NIL);
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
