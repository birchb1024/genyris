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

class SplitMethod(interp: Interpreter?) : AbstractStringMethod(
    interp,
    staticName
) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        var regex = StrinG(" ")
        if (arguments.size > 0) {
            if (arguments[0] !is StrinG) {
                throw GenyrisException("Non string passed to " + Constants.SPLIT)
            }
            regex = arguments[0] as StrinG
        }
        val theString = getSelfString(env)
        return theString.split(NIL, regex)
    }

    companion object {
        val staticName: String
            get() = Constants.SPLIT
    }
}
