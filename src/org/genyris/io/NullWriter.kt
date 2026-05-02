// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import java.io.IOException
import java.io.Writer

class NullWriter : Writer() {
    @kotlin.Throws(IOException::class)
    override fun close() {
    }

    @kotlin.Throws(IOException::class)
    override fun flush() {
    }

    @kotlin.Throws(IOException::class)
    override fun write(cbuf: CharArray?, off: Int, len: Int) {
    }
}
