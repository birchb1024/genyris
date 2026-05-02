// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classification

import org.genyris.core.Dictionary
import org.genyris.core.Exp
import org.genyris.core.StandardClass
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class IsInstanceFunction(interp: Interpreter) : ApplicableFunction(interp, "is-instance?", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment): Exp? {
        checkArguments(arguments, 2)
        checkArgumentTypes(types, arguments)
        StandardClass.Companion.assertIsThisObjectAClass(arguments[1])
        val cw = arguments[1] as StandardClass
        if (cw.isInstance(arguments[0])) return envForBindOperations.getSymbolTable().TRUE()
        else return envForBindOperations.getNil()
    }

    companion object {
        var types: Array<Class<*>?> = arrayOf<Class<*>?>(Exp::class.java, Dictionary::class.java)

        @kotlin.Throws(GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(IsInstanceFunction(interpreter))
        }
    }
}
