// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class InternFunction(interp: Interpreter) : ApplicableFunction(interp, "intern", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        checkArguments(arguments, 1)
        if (arguments[0] is Symbol) {
            return _interp.intern(arguments[0] as Symbol)
        }
        if (arguments[0] is StrinG ||
            arguments[0] is Bignum
        ) {
            return _interp.intern(Symbol.Companion.symbolFactory(arguments[0].toString(), false))
        }
        throw GenyrisException(
            "Unable to intern object " + arguments[0].toString() + " with type " + arguments[0].getClass().getName()
        )
    }
}
