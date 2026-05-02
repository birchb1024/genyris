// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.io.Reader

class ConvertEofInStream(private val _input: InStreamEOF) : InStream {
    private var _haveSavedByte = false
    private var _nextByte: Int


    init {
        _nextByte = (-1.toChar()).code
    }

    @kotlin.Throws(UnsupportedOperationException::class)
    override fun getReader(): Reader? {
        throw UnsupportedOperationException()
    }


    @kotlin.Throws(LexException::class)
    override fun unGet(x: Char) {
        throw LexException("unGet() not implemented in ConvertEofInStream!")
    }

    override fun readNext(): Char {
        val result = _nextByte.toChar()
        _haveSavedByte = false
        return result
    }

    @kotlin.Throws(LexException::class)
    override fun hasData(): Boolean {
        if (_haveSavedByte) return _nextByte != InStreamEOF.Companion.EOF
        else {
            _nextByte = _input.getChar()
            _haveSavedByte = true
            return _nextByte != InStreamEOF.Companion.EOF
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun close() {
        _input.close()
    }

    @kotlin.Throws(LexException::class)
    override fun resetAfterError() {
        _nextByte = (-1.toChar()).code
        _haveSavedByte = false
        _input.resetAfterError()
    }

    override fun withinExpression(env: Environment?) {
        _input.withinExpression(env)
    }

    override fun beginningExpression() {
        _input.beginningExpression()
    }

    override fun getLineNumber(): Int {
        return _input.getLineNUmber()
    }

    override fun getFilename(): String? {
        return _input.getFilename()
    }
}
