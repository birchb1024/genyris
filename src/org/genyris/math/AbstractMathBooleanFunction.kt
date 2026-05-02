// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

abstract class AbstractMathBooleanFunction(interp: Interpreter, name: String?) :
    ApplicableFunction(interp, name, true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        if (arguments.size != 2) {
            throw GenyrisException("Not two arguments to function " + getName())
        }
        val types = arrayOf<Class<*>?>(Bignum::class.java, Bignum::class.java)
        checkArgumentTypes(types, arguments)
        try {
            return mathOperation(arguments[0], arguments[1])
        } catch (e: RuntimeException) {
            throw GenyrisException(e.getMessage())
        }
    }

    protected abstract fun mathOperation(a: Exp?, b: Exp?): Exp?
}
