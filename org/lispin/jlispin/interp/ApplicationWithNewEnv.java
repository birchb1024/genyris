package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.Exp;

public class ApplicationWithNewEnv extends Applicable {

	public Exp apply(Procedure proc, Environment env, Exp[] arguments) throws Exception { // TODO 

		Map bindings = new HashMap();
		for( int i=0 ; i< arguments.length ; i++ ) {
			bindings.put(proc.getArgument(i), arguments[i]);
		}
		Environment newEnv = new Environment(env, bindings);
		return newEnv.evalSequence(proc.getBody());
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
