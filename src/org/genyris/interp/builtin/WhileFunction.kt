// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class WhileFunction(interp: Interpreter) : ApplicableFunction(interp, "while", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        var retval: Exp? = NIL
        this.checkMinArguments(arguments, 1)
        while (arguments[0]!!.eval(env) !== NIL) {
            for (i in 1..<arguments.size) {
                retval = arguments[i]!!.eval(env)
            }
        }
        return retval
    }
}
