// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Constants;
import org.genyris.core.Exp;

public abstract class AbstractMethod extends ApplicableFunction {

	protected Exp _self;
	
    public AbstractMethod(Interpreter interp) {
        super(interp);
    }
    protected void getSelf(Environment env) throws UnboundException {
    	_self = env.lookupVariableValue(_interp.getSymbolTable().internString(Constants.SELF));
    }

}
