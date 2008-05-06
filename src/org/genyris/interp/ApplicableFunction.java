// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;

public abstract class ApplicableFunction {
    protected Interpreter _interp;
    protected Lsymbol     NIL, TRUE;
    protected Exp         _lambda, _lambdam, _lambdaq;
    protected Lsymbol     _name;

    public ApplicableFunction(Interpreter interp, Lsymbol name) {
        _interp = interp;
        NIL = _interp.getNil();
        TRUE = _interp.getTrue();
        _name = name;
        _lambda = interp.getSymbolTable().internPlainString(Constants.LAMBDA);
        _lambdaq = interp.getSymbolTable().internPlainString(Constants.LAMBDAQ);
        _lambdam = interp.getSymbolTable().internPlainString(Constants.LAMBDAM);
    }

    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException;

    public String getName() {
        if (_name == NIL) {
            return this.getClass().getName();
        } else {
            return _name.toString();
        }
    }

    protected Exp arrayToList(Exp[] array) {
        Exp expression = NIL;
        for (int i = array.length - 1; i >= 0; i--) {
            expression = new Lcons(array[i], expression);
        }
        return expression;
    }
}
