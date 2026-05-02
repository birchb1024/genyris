// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.StandardClass
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class DefineClassFunction(interp: Interpreter) : ApplicableFunction(interp, "class", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        checkMinArguments(arguments, 1)
        val types = arrayOf<Class<*>?>(Symbol::class.java)
        checkArgumentTypes(types, arguments)
        val klassname = arguments[0] as Symbol?
        val superklasses = (if (arguments.size > 1) arguments[1] else NIL)
        val newClass: StandardClass = StandardClass.Companion.makeClass(env, klassname, superklasses)
        if (arguments.size > 2) {
            var body = arrayToList(arguments)
            body = body.cdr().cdr()
            body = Pair(klassname, body)
            body.eval(env)
        }
        return newClass
    }
}
