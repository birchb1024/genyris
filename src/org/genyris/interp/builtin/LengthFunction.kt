// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class LengthFunction(interp: Interpreter) : ApplicableFunction(interp, "length", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp>,
        envForBindOperations: Environment?
    ): Exp {
        checkArguments(arguments, 1)
        val s = arguments[0]
        if (s === _interp.NIL) {
            return Bignum(0) // Because an empty list is NIL.
        }
        val allowed = arrayOf<Array<Class<*>?>?>(arrayOf<Class<*>?>(StrinG::class.java, Pair::class.java))
        checkArgumentTypes(allowed, arguments)
        return (Bignum(s.length(NIL)))
    }
}
