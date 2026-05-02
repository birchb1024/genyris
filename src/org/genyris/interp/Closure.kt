// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException

interface Closure {
    // TODO rename this to "Applyable" 
    @kotlin.Throws(GenyrisException::class)
    fun computeArguments(env: Environment?, exp: Exp?): Array<Exp?>?

    @kotlin.Throws(GenyrisException::class)
    fun applyFunction(environment: Environment?, arguments: Array<Exp?>?): Exp?

    override fun toString(): String

    fun getBody(nil: Exp?): Exp?

    fun getPrintableFrame(nIL: Exp?): Exp?
}