// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.Dictionary
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class RemoveTagFunction(interp: Interpreter) : ApplicableFunction(interp, "remove-tag", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment?): Exp {
        checkArguments(arguments, 2)
        val types = arrayOf<Class<*>?>(Dictionary::class.java, Exp::class.java)
        checkArgumentTypes(types, arguments)

        val `object` = arguments[1]
        val newClass: Exp? = arguments[0]
        `object`.removeClass(newClass)
        return `object`
    }
}
