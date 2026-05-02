// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import java.io.UnsupportedEncodingException
import java.io.Writer
import java.net.URLEncoder

class UrlFormatter(out: Writer?) : BasicFormatter(out, true) {
    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG) {
        try {
            write(URLEncoder.encode(lst.toString(), "US-ASCII"))
        } catch (e: UnsupportedEncodingException) {
            throw GenyrisException(e.getMessage())
        }
    }
}
