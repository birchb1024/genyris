// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.PrefixSymbol
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class SymbolPrefixFunction(interp: Interpreter) : ApplicableFunction(interp, "symbol-namespace", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment?): Exp? {
        checkArguments(arguments, 1)
        if (arguments[0] !is PrefixSymbol) {
            return NIL
        }
        val sym = arguments[0] as PrefixSymbol
        return Pair.Companion.cons(StrinG(sym._abbrev), StrinG(sym._prefix))
    }
}
