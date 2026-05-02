// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os

import org.genyris.core.Bignum
import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import java.util.*

class SystemTicksMethod(interp: Interpreter?) : AbstractMethod(interp, "ticks") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment?): Exp {
        val now = Date()
        return Bignum(
            now.getTime().toDouble()
        ) //Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.
    }


    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.OS, SystemTicksMethod(interpreter))
        }
    }
}
