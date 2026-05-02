//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.PrefixSymbol
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException

class ClassloaderFunctions(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_SYSTEM, "load-class-by-name", "sys"), true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        checkArguments(arguments, 1)
        val types = arrayOf<Class<*>?>(StrinG::class.java)
        checkArgumentTypes(types, arguments)
        _interp.loadClassByName(arguments[0].toString())
        return arguments[0]
    }


    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(ClassloaderFunctions(interpreter))
        }
    }
}
