// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.interp.Interpreter;

public class CosFunction extends AbstractMathFunction {

    public CosFunction(Interpreter interp) {
        super(interp,"cos", 1);
    }

    protected Exp mathOperation(Exp a) {
        return ((Bignum)a).cos();
    }
}
