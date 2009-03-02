// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;

public abstract class AbstractMethod extends ApplicableFunction {

    public AbstractMethod(Interpreter interp) {
        super(interp);
    }

    protected Exp _self;

    protected void getSelf(Environment env) throws GenyrisException {
//        _self = env.lookupVariableValue(_interp.getSymbolTable().internPlainString(Constants.SELF));
        _self = env.getSelf();
    }

}
