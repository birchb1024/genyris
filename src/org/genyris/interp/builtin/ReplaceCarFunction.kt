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

class ReplaceCarFunction(interp: Interpreter) : ApplicableFunction(interp, "rplaca", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, argument: Array<Exp?>, envForBindOperations: Environment?): Exp? {
        checkArguments(argument, 2)
        return argument[0]!!.setCar(argument[1])
    }
}
