package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;

public abstract class ApplicableFunction {
	
	protected Interpreter _interp;
	protected Exp NIL, TRUE;

	public ApplicableFunction(Interpreter interp) {
		_interp = interp;
		NIL = _interp.getNil();
		TRUE = _interp.getTrue();
	}

	public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException;

	public String getName() {
		return this.getClass().getName();
	}

	protected Exp arrayToList(Exp[] array) {
		Exp expression = NIL;
		for(int i=array.length-1; i >= 0 ; i--) { 
			expression = new Lcons( array[i], expression); 
		}
	return expression;
	}
}
