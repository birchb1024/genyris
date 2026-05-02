// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.TailCall

class TailCallFunction(interp: Interpreter?) : BuiltinFunction(interp, "tailcall", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp {
        if (arguments.size == 0) {
            throw GenyrisException("tailcall expected closure as first argument but got nothing.")
        }
        if (arguments[0] !is Closure) {
            throw GenyrisException("tailcall expected closure as first argument but got: " + arguments[0])
        }
        val newargs = arrayOfNulls<Exp>(arguments.size - 1)
        System.arraycopy(arguments, 1, newargs, 0, newargs.size)
        return TailCall(arguments[0] as Closure, newargs)
    }
}
