// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.interp.Interpreter;

public class PowerFunction extends AbstractMathFunction {

    public PowerFunction(Interpreter interp) {
        super(interp,"power", 2);
    }

    protected Exp mathOperation(Exp a, Exp b) {
        return ((Bignum)a).pow((Bignum) b);
    }
}
