// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import java.math.BigDecimal;
import java.math.MathContext;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
// TODO DRY with other 2-arg maths functions.
public class PowerFunction extends ApplicableFunction {

    public PowerFunction(Interpreter interp, Lsymbol name) {
        super(interp, name);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length < 2)
            throw new GenyrisException("Too few arguments to /: " + arguments.length);
        try {
            Exp result = arguments[0];
            for( int i=1; i< arguments.length; i++ ) {
                result = powAux(result, arguments[i]);
            }
            return result;
        }
        catch(RuntimeException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    private Exp powAux(Exp a, Exp b) {
        // TODO make plus work for combiations of int, double and BigDecimal
        return new Bignum(((BigDecimal) a.getJavaValue()).pow(((BigDecimal) b.getJavaValue()).intValueExact(), new MathContext(100000)));
    }
}