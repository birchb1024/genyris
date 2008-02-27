// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import java.math.BigDecimal;

import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class GreaterThanFunction extends ApplicableFunction {


    public GreaterThanFunction(Interpreter interp, Lsymbol name) {
        super(interp, name);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length != 2)
            throw new GenyrisException("Not two arguments to >: " + arguments.length);
        try {
            return gtAux(arguments[0], arguments[1]);
        }
        catch(RuntimeException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    private Exp gtAux(Exp a, Exp b) {
        // TODO make plus work for combiations of int, double and BigDecimal
        if ( ((BigDecimal) a.getJavaValue()).compareTo((BigDecimal) b.getJavaValue()) > 0 ) {
            return TRUE;
        }
        else {
            return NIL;
        }
    }
}
