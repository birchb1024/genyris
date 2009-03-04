// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public abstract class ApplicableFunction {
    protected Interpreter _interp;
    protected Symbol     NIL, TRUE;
    protected Exp         _lambda, _lambdam, _lambdaq;
    private String  _name;
    
    public ApplicableFunction(Interpreter interp, String name) {
    	_name = name;
        _interp = interp;
        NIL = _interp.getNil();
        TRUE = _interp.getTrue();
        _lambda = interp.getSymbolTable().internPlainString(Constants.LAMBDA);
        _lambdaq = interp.getSymbolTable().internPlainString(Constants.LAMBDAQ);
        _lambdam = interp.getSymbolTable().internPlainString(Constants.LAMBDAM);
    }

    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException;

	protected Exp arrayToList(Exp[] array) {
        Exp expression = NIL;
        for (int i = array.length - 1; i >= 0; i--) {
            expression = new Lcons(array[i], expression);
        }
        return expression;
    }

	public String getName() {
		return _name;
	}

	protected void checkArguments(Exp[] arguments, int exactly) throws GenyrisException {
		checkArguments(arguments, exactly,exactly);
	}
	protected void checkArguments(Exp[] arguments, int minimum, int maximum) throws GenyrisException {
        if( arguments.length < minimum ||  arguments.length > maximum)
            throw new GenyrisException("Incorrect number of arguments to " + getName() + " was expecting between " + minimum + " and " + maximum + "." );
	}
}
