// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.exception

import org.genyris.core.Exp
import org.genyris.core.StrinG

open class GenyrisException : Exception {
    protected var _reason: Exp?
    var filename: String? = null
    var lineNumber: Int = 0

    constructor(e: Exception) : super(e) {
        var message: String? = e.getMessage()
        if (message == null) {
            message = e.toString()
        }
        this._reason = StrinG(message)
    }

    constructor(message: String?) : super(message) {
        this._reason = StrinG(message)
    }

    constructor(e: Exception, filename: String?, lineNumber: Int) : super(e) {
        var message: String? = e.getMessage()
        if (message == null) {
            message = e.toString()
        }
        this._reason = StrinG(message)
        this.filename = filename
        this.lineNumber = lineNumber
    }

    constructor(message: String?, filename: String?, lineNumber: Int) : super(message) {
        this._reason = StrinG(message)
        this.filename = filename
        this.lineNumber = lineNumber
    }

    constructor(data: Exp) : super(data.toString()) {
        this._reason = data
    }

    val message: String
        get() {
            var tmp = ""
            if (this.filename != null) {
                tmp += this.filename + ":" + Integer.toString(this.lineNumber) + " "
            }
            if (_reason != null) {
                tmp += super.getMessage()
            }
            return tmp
        }

    val data: Exp
        get() = if (_reason != null) _reason else StrinG("null")

    companion object {
        private const val serialVersionUID = 2930499792506317096L
    }
}
