// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException

abstract class AbstractMethod(interp: Interpreter, name: String?) : ApplicableFunction(interp, name, true) {
    var _self: Exp? = null

    @kotlin.Throws(GenyrisException::class)
    fun getSelf(env: Environment) {
        _self = env.getSelf()
    }
}
