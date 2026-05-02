// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class SynchronizeFunction(interp: Interpreter?) : TaskFunction(interp, "synchronized", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        env: Environment?
    ): Exp? {
        checkMinArguments(arguments, 2)
        var retval: Exp? = NIL
        synchronized(arguments[0]!!.eval(env)) {
            for (i in 1..<arguments.size) {
                retval = arguments[i]!!.eval(env)
            }
            return retval
        }
    }
}