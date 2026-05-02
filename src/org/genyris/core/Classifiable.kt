// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.interp.Environment

interface Classifiable {
    fun getClasses(env: Environment?): Exp?
    fun addClass(klass: Dictionary?)
    fun removeClass(klass: Exp?)
    fun isTaggedWith(klass: Dictionary?): Boolean

    @kotlin.Throws(AccessException::class)
    fun setClasses(classList: Exp?, NIL: Exp?)
    fun getBuiltinClassSymbol(table: Internable?): Symbol?
}
