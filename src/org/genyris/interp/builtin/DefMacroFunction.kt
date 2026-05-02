// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class DefMacroFunction(interp: Interpreter) : ApplicableFunction(interp, "defmacro", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment
    ): Exp {
        checkMinArguments(arguments, 2)
        val types = arrayOf<Class<*>?>(Symbol::class.java)
        checkArgumentTypes(types, arguments)
        checkFormalArgumentSyntax(arguments[1])
        val lambdaExpression: Exp = Pair(_lambdam, arrayToList(arguments).cdr())
        val fn = LazyProcedure(
            envForBindOperations,
            lambdaExpression, MacroFunction(arguments[0].toString(), _interp)
        )
        envForBindOperations.defineVariable(arguments[0] as Symbol?, fn)
        return fn
    }
}
