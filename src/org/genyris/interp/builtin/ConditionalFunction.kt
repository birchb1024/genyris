// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class ConditionalFunction(interp: Interpreter) : ApplicableFunction(interp, "cond", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        for (i in arguments.indices) {
            if (arguments[i] !is Pair) {
                throw GenyrisException("Invalid condition: " + arguments[i])
            }
            val condition = arguments[i]!!.car().eval(env)
            if (condition !== NIL) {
                if (arguments[i]!!.cdr() === NIL) {
                    return condition
                } else {
                    if (arguments[i] !is Pair) {
                        throw GenyrisException("Invalid condition: " + arguments[i])
                    }
                    return arguments[i]!!.cdr().evalSequence(env)
                }
            }
        }
        return NIL
    }
}
