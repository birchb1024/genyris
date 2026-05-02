// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Symbol
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class ApplyFunction(interp: Interpreter?) : BuiltinFunction(interp, "apply", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp>,
        envForBindOperations: Environment?
    ): Exp? {
        checkArguments(arguments, 2)
        val functionToApply: Closure = arguments[0]
        val args = makeArray(arguments[1], NIL)
        return functionToApply.applyFunction(envForBindOperations, args)
    }

    @kotlin.Throws(AccessException::class)
    private fun makeArray(list: Exp, NIL: Symbol?): Array<Exp?> {
        // TODO - Refactor for efficiency?
        var list = list
        var i = 0
        val result = arrayOfNulls<Exp>(list.length(NIL))
        while (list !== NIL) {
            result[i] = list.car()
            list = list.cdr()
            i++
        }
        return result
    }
}
