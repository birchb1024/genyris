// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.interp.Interpreter

class LessThanFunction(interp: Interpreter?) : AbstractMathBooleanFunction(interp, "<") {
    override fun mathOperation(a: Exp, b: Exp?): Exp? {
        return if ((a as Bignum).lessThan(b as Bignum?)) TRUE else NIL
    }
}
