// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class AsStringFunction(interp: Interpreter) : ApplicableFunction(interp, "asString", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        environment: Environment?
    ): Exp {
        checkArguments(arguments, 1)
        if (arguments[0] is Symbol) {
            val sym = arguments[0] as Symbol
            return StrinG(sym.getPrintName())
        }
        return StrinG(arguments[0].toString())
    }
}
