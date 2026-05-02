// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.interp.Interpreter

class CosFunction(interp: Interpreter?) : AbstractMathFunction(interp, "cos", 1) {
    override fun mathOperation(a: Exp): Exp? {
        return (a as Bignum).cos()
    }
}
