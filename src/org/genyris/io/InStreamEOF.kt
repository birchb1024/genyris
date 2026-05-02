// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment

interface InStreamEOF {
    @get:kotlin.Throws(LexException::class)
    val char: Int

    @kotlin.Throws(GenyrisException::class)
    fun close()

    @kotlin.Throws(LexException::class)
    fun resetAfterError()

    fun withinExpression(env: Environment?)

    fun beginningExpression()

    val lineNUmber: Int

    val filename: String?

    companion object {
        val EOF: Int = -1
    }
}
