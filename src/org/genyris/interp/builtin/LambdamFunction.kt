// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class LambdamFunction(interp: Interpreter) : ApplicableFunction(interp, Constants.LAMBDAM, false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        checkFormalArgumentSyntax(arguments[0])
        var expression = arrayToList(arguments)
        expression = Pair(_lambdam, expression)
        return LazyProcedure(env, expression, MacroFunction("anonymous lambdam", _interp))
    }
}
