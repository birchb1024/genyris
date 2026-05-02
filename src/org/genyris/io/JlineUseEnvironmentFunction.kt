// Copyright 2019 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class JlineUseEnvironmentFunction(interp: Interpreter) :
    ApplicableFunction(interp, Constants.PREFIX_SYSTEM + "jline-use-current-environment", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment?): Exp? {
        this.checkArguments(arguments, 0, 0)
        val the_jline: JlineStdioInStream = JlineStdioInStream.Companion.knew()
        the_jline.setEnvironment(env)
        return NIL
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(JlineUseEnvironmentFunction(interpreter))
        }
    }
}
