// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException
import org.genyris.io.parser.StreamParser.AbstractParserMethod
import org.genyris.io.readerstream.ReaderStream

class StringFormatStream(var _instream: InStream) : InStreamEOF {
    var _parseState: Int
    var _escaped: Char = 0.toChar()

    init {
        _parseState = STARTING
    }

    @kotlin.Throws(LexException::class)
    override fun getChar(): Int {
        var ch = '@'
        when (_parseState) {
            IN_A_STRING -> {
                if (!_instream.hasData()) {
                    _parseState = ENDING
                    return '"'.code
                }
                ch = _instream.readNext()
                if (ch == '\\') {
                    _parseState = ESCAPE
                    _escaped = '\\'
                    return '\\'.code
                } else if (ch == '"') {
                    _parseState = ESCAPE
                    _escaped = '"'
                    return '\\'.code
                } else if (ch == '#') {
                    if (!_instream.hasData()) {
                        _parseState = ENDING
                        return ch.code
                    }
                    val ch2 = _instream.readNext()
                    if (ch2 == '{') {
                        _parseState = IN_LISP
                        return '"'.code // end current string
                    } else {
                        _instream.unGet(ch2) // parse this char again
                        return '#'.code
                    }
                } else {
                    return ch.code
                }
            }

            IN_LISP -> {
                if (!_instream.hasData()) {
                    _parseState = ENDING
                    return ch.code
                }
                ch = _instream.readNext()
                if (ch == '}') {
                    _parseState = IN_A_STRING
                    return '"'.code // start new string
                } else {
                    return ch.code
                }
            }

            STARTING -> {
                _parseState = IN_A_STRING
                ch = '"'
                return ch.code
            }

            ENDING -> return InStreamEOF.Companion.EOF
            ESCAPE -> {
                _parseState = IN_A_STRING
                return _escaped.code
            }
        }
        return InStreamEOF.Companion.EOF
    }

    @kotlin.Throws(GenyrisException::class)
    override fun close() {
    }

    class NewMethod(interp: Interpreter?) : AbstractParserMethod(interp, "new") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
            val types = arrayOf<Class<*>?>(ReaderStream::class.java)
            this.checkArgumentTypes(types, arguments)
            val input = arguments[0] as ReaderStream
            return ReaderStream(StringFormatStream(input.getInStream()))
        }
    }

    override fun resetAfterError() {
    }

    override fun withinExpression(env: Environment?) {
        this._instream.withinExpression(null)
    }

    override fun beginningExpression() {
        this._instream.beginningExpression()
    }

    override fun getLineNUmber(): Int {
        return _instream.getLineNumber()
    }

    override fun getFilename(): String? {
        return _instream.getFilename()
    }

    companion object {
        private const val IN_A_STRING = 0
        private const val STARTING = 1
        private const val ENDING = 2
        private const val ESCAPE = 3
        private const val IN_LISP = 4

        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance("StringFormatStream", NewMethod(interpreter))
        }
    }
}