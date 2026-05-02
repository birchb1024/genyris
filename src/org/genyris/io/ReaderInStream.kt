// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.io.IOException
import java.io.PushbackReader
import java.io.Reader

class ReaderInStream(reader: Reader, filename: String?) : InStream {
    private var _nextByte = 0
    private var _haveChar: Boolean
    private val _reader: PushbackReader
    private var _lineCount: Int
    private val _filename: String?

    init {
        _reader = PushbackReader(reader)
        _haveChar = false
        _lineCount = 1
        _filename = filename
    }

    override fun getReader(): Reader {
        return _reader
    }

    @kotlin.Throws(LexException::class)
    override fun unGet(x: Char) {
        if (x == '\n') {
            _lineCount--
        }
        val charArray = CharArray(1)
        charArray[0] = x
        try {
            _reader.unread(charArray)
        } catch (e: IOException) {
            throw LexException(e.getMessage())
        }
    }

    override fun readNext(): Char {
        _haveChar = false
        if (_nextByte == '\n'.code) {
            _lineCount++
        }
        return _nextByte.toChar()
    }

    override fun hasData(): Boolean {
        try {
            if (_haveChar) {
                return true
            }
            if (!_reader.ready()) {
                return false
            }
            _nextByte = _reader.read()
        } catch (e: IOException) {
            return false
        }
        if (_nextByte == -1) {
            _haveChar = false
            return false
        } else {
            _haveChar = true
            return true
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun close() {
        try {
            _reader.close()
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
    }

    override fun resetAfterError() {
        _haveChar = false
    }

    override fun withinExpression(env: Environment?) {
    }

    override fun beginningExpression() {
    }

    override fun getLineNumber(): Int {
        return _lineCount
    }

    override fun getFilename(): String? {
        return _filename
    }
}
