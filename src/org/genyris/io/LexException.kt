// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException

class LexException : GenyrisException {
    constructor(string: String?) : super(string)

    constructor(msg: String?, filename: String?, lineNumber: Int) : super(msg, filename, lineNumber)

    companion object {
        /**
         *
         */
        private val serialVersionUID = -431332335287928314L
    }
}
