// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.interp.Interpreter;

public class SinFunction extends AbstractMathFunction {

    public SinFunction(Interpreter interp) {
        super(interp,"sin", 1);
    }

    protected Exp mathOperation(Exp a) {
        return ((Bignum)a).sin();
    }
}
