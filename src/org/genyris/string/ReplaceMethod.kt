// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class ReplaceMethod(interp: Interpreter?) : AbstractStringMethod(interp, "replace") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        val types = arrayOf<Class<*>?>(StrinG::class.java, StrinG::class.java)
        this.checkArgumentTypes(types, arguments)
        if (arguments.size > 0) {
            if (arguments[0] !is StrinG) {
                throw GenyrisException("Non string passed to replace")
            }
        }
        val regex = arguments[0] as StrinG
        val replacement = arguments[1] as StrinG
        val theString = getSelfString(env)
        return theString.replace(regex, replacement)
    }
}
