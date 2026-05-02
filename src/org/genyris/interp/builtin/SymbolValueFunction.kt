// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class SymbolValueFunction(interp: Interpreter) : ApplicableFunction(interp, "symbol-value", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment): Exp? {
        checkArguments(arguments, 1)
        val types = arrayOf<Class<*>?>(Symbol::class.java)
        checkArgumentTypes(types, arguments)
        return envForBindOperations.lookupVariableValue(arguments[0] as Symbol?)
    }
}
