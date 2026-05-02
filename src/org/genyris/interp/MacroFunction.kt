// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.Biscuit
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException

class MacroFunction(name: String?, interp: Interpreter?) : ClassicFunction(name, interp) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(closure: Closure?, arguments: Array<Exp?>?, env: Environment?): Exp {
        if (closure !is AbstractClosure) {
            throw GenyrisException("type missmatch - was expecting an AbstractClosure")
        }
        val proc = closure
        return Biscuit(super.bindAndExecute(proc, arguments, proc.getEnv()))
    }
}
