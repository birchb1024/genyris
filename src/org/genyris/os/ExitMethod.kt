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

class ExitMethod(interp: Interpreter?) : AbstractMethod(interp, "exit") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        var status = 0
        if (arguments.size > 1) {
            checkArguments(arguments, 1)
            val types = arrayOf<Class<*>?>(Bignum::class.java)
            checkArgumentTypes(types, arguments)
            status = (arguments[0] as Bignum).bigDecimalValue().intValue()
            return NIL
        }
        //Runtime.getRuntime().halt(0);
        System.exit(status)
        return NIL
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.OS, ExitMethod(interpreter))
        }
    }
}
