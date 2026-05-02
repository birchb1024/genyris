// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.interp.Interpreter

class Atan2Function(interp: Interpreter?) : AbstractMathFunction(interp, "atan2", 2) {
    override fun mathOperation(a: Exp, b: Exp?): Exp? {
        return (a as Bignum).atan2(b as Bignum?)
    }
}
