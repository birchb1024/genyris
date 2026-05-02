// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.GenyrisException


open class EscapedSymbol(name: String?) : SimpleSymbol(name) {
    override fun toString(): String {
        return "|" + getPrintName() + "|"
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitFullyQualifiedSymbol(this)
    }
}
