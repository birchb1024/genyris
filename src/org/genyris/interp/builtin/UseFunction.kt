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

class UseFunction(interp: Interpreter) : ApplicableFunction(interp, "use", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(ignored: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        this.checkMinArguments(arguments, 2)
        val `object` = arguments[0]!!.eval(env)
        val newEnv = `object`.makeEnvironment(env)
        var retval: Exp? = NIL
        for (i in 1..<arguments.size) {
            retval = arguments[i]!!.eval(newEnv)
        }
        return retval
    }
}
