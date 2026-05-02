// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.io.CharArrayReader
import java.io.Reader

class StringInStream(astring: String) : InStream {
    private val _value: CharArray
    private var _readPointer = 0
    private var _lineCount = 1

    init {
        _value = astring.toCharArray()
    }

    @kotlin.Throws(UnsupportedOperationException::class)
    override fun getReader(): Reader {
        return CharArrayReader(_value)
    }

    override fun hasData(): Boolean {
        return this._readPointer < _value.size
    }

    override fun readNext(): Char {
        if (_value[_readPointer] == '\n') {
            _lineCount++
        }
        return _value[_readPointer++]
    }

    @kotlin.Throws(LexException::class)
    override fun unGet(x: Char) {
        throw LexException("PipeInStream: unGet not supported!")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun close() {
    }

    override fun resetAfterError() {
    }

    override fun withinExpression(env: Environment?) {
    }

    override fun beginningExpression() {
    }

    override fun getLineNumber(): Int {
        return this._lineCount
    }

    override fun getFilename(): String {
        return "StringInStream " + hashCode()
    }
}
