// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.interp.Interpreter;

public class MultiplyFunction extends AbstractMathFunction {

     public MultiplyFunction(Interpreter interp) {
        super(interp,  "*", 2);
    }

    protected Exp mathOperation(Exp a, Exp b) {
		return ((Bignum) a).multiply((Bignum) b);
    }

}
