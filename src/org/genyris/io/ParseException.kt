// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException

class ParseException : GenyrisException {
    constructor(string: String?) : super(string)

    constructor(string: String?, filename: String?, lineNumber: Int) : super(string, filename, lineNumber)

    companion object {
        private const val serialVersionUID = 3268672144858986389L
    }
}
