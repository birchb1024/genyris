// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import java.io.Writer

class DisplayFormatter(out: Writer?) : BasicFormatter(out) {
    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG) {
        write(lst.toString())
    }
}
