package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;

public class ClassicFunction extends ApplicableFunction {

	public Exp bindAndExecute(Procedure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException  { 

		Map bindings = new HashMap();
		if(arguments.length < proc.getNumberOfRequiredArguments()) {
			throw new LispinException("Too few arguments supplied to proc: " + proc.getName());
		}
		for( int i=0 ; i< arguments.length ; i++ ) {
			if( proc.getArgument(i) != SymbolTable.NIL )
				bindings.put(proc.getArgument(i), arguments[i]);
		}
		Environment newEnv = new Environment(proc.getEnv(), bindings); // Use the procedure's frame to get lexical scope
		return newEnv.evalSequence(proc.getBody());
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
