// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class NthFunction(interp: Interpreter) : ApplicableFunction(interp, "nth", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment?): Exp? {
        checkArguments(arguments, 2)
        val types = arrayOf<Class<*>?>(Bignum::class.java, Exp::class.java)
        checkArgumentTypes(types, arguments)
        return arguments[1]!!.nth((arguments[0] as Bignum).bigDecimalValue().intValue(), NIL)
    }
}
