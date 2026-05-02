// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment

interface InStream {
    @kotlin.Throws(LexException::class)
    fun unGet(x: Char)

    @kotlin.Throws(LexException::class)
    fun readNext(): Char

    @kotlin.Throws(LexException::class)
    fun hasData(): Boolean

    @kotlin.Throws(GenyrisException::class)
    fun close()

    @kotlin.Throws(LexException::class)
    fun resetAfterError()
    fun withinExpression(env: Environment?)
    fun beginningExpression()
    val lineNumber: Int
    val filename: String?

    @get:kotlin.Throws(UnsupportedOperationException::class)
    val reader: Reader?
}
