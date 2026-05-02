// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class FromIntsMethod(interp: Interpreter?) : AbstractStringMethod(
    interp,
    staticName
) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        val types = arrayOf<Class<*>?>(StrinG::class.java, Exp::class.java)
        checkArgumentTypes(types, arguments)
        return StrinG.Companion.makeStringFromCharset(NIL, arguments[1], arguments[0].toString())
    }

    companion object {
        val staticName: String
            get() = Constants.FROMINTS
    }
}
