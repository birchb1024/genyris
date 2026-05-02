// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.logic

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class OrFunction(interp: Interpreter) : ApplicableFunction(interp, "or", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment?): Exp? {
        if (arguments.size < 2) throw GenyrisException("Too few arguments to or: " + arguments.size)
        var result: Exp? = NIL
        for (i in arguments.indices) {
            result = arguments[i]!!.eval(envForBindOperations)
            if (result !== NIL) {
                return result
            } else {
                continue
            }
        }
        return NIL
    }
}
