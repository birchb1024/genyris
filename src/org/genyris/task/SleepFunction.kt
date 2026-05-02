// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.exception.GenyrisInterruptedException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class SleepFunction(interp: Interpreter?) : TaskFunction(interp, "sleep", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        checkArguments(arguments, 1)
        try {
            Thread.sleep((arguments[0] as Bignum).bigDecimalValue().longValue())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw GenyrisInterruptedException(e.getMessage())
        }
        return NIL
    }
}