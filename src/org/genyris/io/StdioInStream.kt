// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.io.IOException
import java.io.Reader

class StdioInStream private constructor() : InStream {
    //
    // WARNING - this class has only one instance shared between all threads.
    //
    private var _nextByte = 0
    private var _gotByte = false
    private var _lineCount = 1

    @kotlin.Throws(UnsupportedOperationException::class)
    override fun getReader(): Reader? {
        throw UnsupportedOperationException()
    }

    @kotlin.jvm.Synchronized
    @kotlin.Throws(LexException::class)
    override fun unGet(x: Char) {
        throw LexException("StdioStream: unGet not implemented.")
    }


    @kotlin.jvm.Synchronized
    @kotlin.Throws(LexException::class)
    override fun readNext(): Char {
        if (!_gotByte) {
            throw LexException("StdioInStream: readNext() called before hasData()")
        }
        _gotByte = false
        if (_nextByte == '\n'.code) {
            _lineCount++
        }
        return _nextByte.toChar()
    }

    @kotlin.jvm.Synchronized
    override fun hasData(): Boolean {
        try {
            if (_gotByte) {
                return true
            }
            _nextByte = System.`in`.read()
            _gotByte = true
        } catch (e: IOException) {
            _gotByte = false
            return false
        }
        if (_nextByte == -1) {
            _gotByte = false
            return false
        } else {
            return true
        }
    }

    @kotlin.jvm.Synchronized
    @kotlin.Throws(GenyrisException::class)
    override fun close() {
    }

    @kotlin.jvm.Synchronized
    override fun resetAfterError() {
        _gotByte = false
    }

    override fun withinExpression(env: Environment?) {
    }

    override fun beginningExpression() {
    }

    override fun getLineNumber(): Int {
        return _lineCount
    }

    override fun getFilename(): String {
        return "stdin"
    }

    companion object {
        private var singleton: StdioInStream? = null

        @kotlin.jvm.Synchronized
        fun knew(): StdioInStream { // the 'k' is silent.
            if (singleton == null) {
                singleton = StdioInStream()
            }
            return singleton!!
        }
    }
}
