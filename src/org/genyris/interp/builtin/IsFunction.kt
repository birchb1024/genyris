// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.StandardClass
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class IsFunction(interp: Interpreter) : ApplicableFunction(interp, "is?", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        environment: Environment
    ): Exp? {
        checkArguments(arguments, 2)
        val types = arrayOf<Class<*>?>(Exp::class.java, StandardClass::class.java)
        checkArgumentTypes(types, arguments)
        val `object` = arguments[0]
        val klass = arguments[1] as StandardClass
        TagFunction.Companion.validateObjectInClass(environment, `object`, klass)
        return `object`
    }
}
