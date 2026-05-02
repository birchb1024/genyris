// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment

class NilSymbol : SimpleSymbol(Constants.NIL) {
    override fun isNil(): Boolean {
        return true
    }

    val isMember: Boolean
        get() = false

    override fun toString(): String {
        return _printName
    }

    @kotlin.Throws(GenyrisException::class)
    override fun evalSequence(env: Environment?): Exp {
        return this
    }

    @kotlin.Throws(AccessException::class)
    override fun length(NIL: Symbol?): Int {
        return 0
    }

    @kotlin.Throws(AccessException::class)
    override fun nth(number: Int, NIL: Symbol?): Exp? {
        throw AccessException("nth called on nil.")
    }
}
