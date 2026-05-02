// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Constants
import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.SimpleSymbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class DynamicSymbolValueFunction(interp: Interpreter) : ApplicableFunction(interp, Constants.DYNAMIC_SYMBOL, true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp {
        val types = arrayOf<Class<*>?>(SimpleSymbol::class.java)
        checkArguments(arguments, 1)
        checkArgumentTypes(types, arguments)
        return DynamicSymbol(arguments[0] as SimpleSymbol?)
    }
}
