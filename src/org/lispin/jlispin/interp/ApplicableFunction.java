package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;

public abstract class ApplicableFunction {
	
	public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException;

	public String getName() {
		return this.getClass().getName();
	}

	protected Exp arrayToList(Exp[] array, Exp NIL) {
		Exp expression = NIL;
		for(int i=array.length-1; i >= 0 ; i--) { 
			expression = new Lcons( array[i], expression); 
		}
	return expression;
	}
}
