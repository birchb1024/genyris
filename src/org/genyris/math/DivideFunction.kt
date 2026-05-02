// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.interp.Interpreter

class DivideFunction(interp: Interpreter?) : AbstractMathFunction(interp, "/", 2) {
    override fun mathOperation(a: Exp, b: Exp?): Exp? {
        return (a as Bignum).divide(b as Bignum?)
    }
}
