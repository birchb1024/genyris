// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.io.Reader

class UngettableInStream : InStream {
    private val _input: InStream
    private val _ungetChars: CharArray
    private var _readPointer: Int

    constructor(aStream: InStream, size: Int) {
        _input = aStream
        _ungetChars = CharArray(size)
        _readPointer = -1
    }

    constructor(aStream: InStream) {
        _input = aStream
        _ungetChars = CharArray(10)
        _readPointer = -1
    }

    override fun getReader(): Reader? {
        return _input.getReader()
    }

    private fun bufferEmpty(): Boolean {
        return _readPointer < 0
    }

    private fun bufferFull(): Boolean {
        return _readPointer >= _ungetChars.size - 1
    }

    @kotlin.Throws(LexException::class)
    override fun hasData(): Boolean {
        return !bufferEmpty() || _input.hasData()
    }

    @kotlin.Throws(LexException::class)
    override fun readNext(): Char {
        if (bufferEmpty()) {
            return _input.readNext()
        } else {
            return _ungetChars[_readPointer--]
        }
    }

    @kotlin.Throws(LexException::class)
    override fun unGet(x: Char) {
        if (bufferFull()) {
            throw LexException("too many characters pushed back on ungettable stream")
        } else {
            _readPointer++
            _ungetChars[_readPointer] = x
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun close() {
        _input.close()
    }

    @kotlin.Throws(LexException::class)
    override fun resetAfterError() {
        _readPointer = -1
        _input.resetAfterError()
    }

    override fun withinExpression(env: Environment?) {
        _input.withinExpression(env)
    }

    override fun beginningExpression() {
        _input.beginningExpression()
    }

    override fun getLineNumber(): Int {
        var numberLinesPreviewed = 0
        for (i in _ungetChars.indices) {
            if (_ungetChars[i] == '\n') {
                numberLinesPreviewed++
            }
        }
        return _input.getLineNumber() - numberLinesPreviewed
    }

    override fun getFilename(): String? {
        return _input.getFilename()
    }
}

