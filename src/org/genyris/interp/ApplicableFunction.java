package org.genyris.interp;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;

public abstract class ApplicableFunction {
	
	protected Interpreter _interp;
	protected Exp NIL, TRUE;
	protected Exp _lambda, _lambdam, _lambdaq;

	public ApplicableFunction(Interpreter interp) {
		_interp = interp;
		NIL = _interp.getNil();
		TRUE = _interp.getTrue();
		_lambda = interp.getSymbolTable().internString(Constants.LAMBDA);
		_lambdaq = interp.getSymbolTable().internString(Constants.LAMBDAQ);
		_lambdam = interp.getSymbolTable().internString(Constants.LAMBDAM);
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
