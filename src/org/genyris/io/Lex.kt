// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.math.BigDecimal

class Lex {
    private var _input: InStream? = null

    private var _mapper: PrefixMapper? = null

    private var _symbolTable: Internable? = null

    private var _cdrCharacter = 0.toChar()
    private var _commentCharacter = 0.toChar()

    var EOF_TOKEN: Symbol? = null
    var QUOTE_TOKEN: Symbol? = null
    var DYNAMIC_TOKEN: Symbol? = null
    var BACKQUOTE_TOKEN: Symbol? = null
    var DOLLAR_AT_TOKEN: Symbol? = null

    var DOLLAR_TOKEN: Symbol? = null

    var LEFT_PAREN_TOKEN: Symbol? = null
    var RIGHT_PAREN_TOKEN: Symbol? = null
    var CDR_TOKEN: Symbol? = null
    var LEFT_SQUARE_TOKEN: Symbol? = null
    var RIGHT_SQUARE_TOKEN: Symbol? = null
    var LEFT_CURLY_TOKEN: Symbol? = null
    var RIGHT_CURLY_TOKEN: Symbol? = null
    var PLING_TOKEN: Symbol? = null
    var SEMI_TOKEN: Symbol? = null

    private var _dynamicCharacter = 0.toChar()

    private fun init(inputSource: InStream, table: Internable, dynaChar: Char, cdrChar: Char, commentChar: Char) {
        _mapper = PrefixMapper(dynaChar)
        _input = inputSource
        _symbolTable = table
        _cdrCharacter = cdrChar
        _commentCharacter = commentChar
        _dynamicCharacter = dynaChar

        QUOTE_TOKEN = SimpleSymbol("QuoteToken")
        BACKQUOTE_TOKEN = SimpleSymbol("BackquoteToken")
        DOLLAR_AT_TOKEN = SimpleSymbol("DOLLAR_AT_TOKEN")
        DOLLAR_TOKEN = SimpleSymbol("DOLLAR_TOKEN")
        DYNAMIC_TOKEN = SimpleSymbol("DYNAMIC_TOKEN")
        EOF_TOKEN = SimpleSymbol("EOF_TOKEN")
        LEFT_PAREN_TOKEN = SimpleSymbol("leftParenToken")
        RIGHT_PAREN_TOKEN = SimpleSymbol("righParenToken")
        LEFT_SQUARE_TOKEN = SimpleSymbol("leftSquareToken")
        RIGHT_SQUARE_TOKEN = SimpleSymbol("rightSquareToken")
        LEFT_CURLY_TOKEN = SimpleSymbol("leftCurlyToken")
        RIGHT_CURLY_TOKEN = SimpleSymbol("rightCurlyToken")
        CDR_TOKEN = SimpleSymbol("pair-delimiterToken")
        PLING_TOKEN = SimpleSymbol("plingToken")
        SEMI_TOKEN = SimpleSymbol("semiToken") // short for semicolon
    }

    constructor(inputSource: InStream, table: Internable, dynaChar: Char, cdrChar: Char, commentChar: Char) {
        init(inputSource, table, dynaChar, cdrChar, commentChar)
    }

    constructor(inputSource: InStream, table: SymbolTable) {
        init(inputSource, table, Constants.DYNAMICSCOPECHAR2, Constants.CDRCHAR, Constants.COMMENTCHAR)
    }

    @kotlin.Throws(LexException::class)
    fun parseDecimalNumber(): BigDecimal {
        val collect = StringBuffer()
        var ch: Char
        if (!_input!!.hasData()) {
            throw lexError("unexpected end of file")
        }
        ch = _input!!.readNext()
        if (ch == '-') {
            collect.append(ch)
        } else {
            _input!!.unGet(ch)
        }
        while (_input!!.hasData()) {
            ch = _input!!.readNext()
            if ((ch <= '9' && ch >= '0') || (ch == '.')) {
                collect.append(ch)
            } else {
                _input!!.unGet(ch)
                break
            }
        }
        try {
            return BigDecimal(collect.toString())
        } catch (e: NumberFormatException) {
            throw lexError("NumberFormatException on " + collect.toString())
        }
    }

    @kotlin.Throws(LexException::class)
    fun parseNumber(): Exp {
        val floatingValue: BigDecimal
        val nextChar: Char
        floatingValue = parseDecimalNumber()
        if (!this._input!!.hasData()) {
            return Bignum(floatingValue)
        }

        val mantissa: BigDecimal
        nextChar = _input!!.readNext()
        if (nextChar == 'e' || nextChar == 'E') {
            mantissa = parseDecimalNumber()
            val mantissaRaised = Math.pow(10.0, mantissa.intValue().toDouble())
            return (Bignum(floatingValue.doubleValue() * mantissaRaised))
        } else {
            _input!!.unGet(nextChar)
            return Bignum(floatingValue)
        }
    }

    private fun isNotIdentEscapeChar(c: Char): Boolean {
        when (c) {
            '\f', '\n', '\t', '\r' -> return false
            else -> return true
        }
    }

    fun isIdentCharacter(c: Char): Boolean {
        if (c == _cdrCharacter) return false

        if (c == _commentCharacter) return false

        if (c == _dynamicCharacter) return false

        when (c) {
            '\f', '\n', '\t', ' ', '\r', '(', ')', '[', ']', '{', '}', Constants.BQUOTECHAR, Constants.QUOTECHAR, '\'', '"', '!', ';' -> return false
            else -> return true
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun parseIdentEscaped(): Exp? {
        var ch: Char
        val collect = StringBuffer()
        if (!_input!!.hasData()) {
            throw lexError("unexpected end of file")
        }
        while (_input!!.hasData()) {
            ch = _input!!.readNext()
            if (isNotIdentEscapeChar(ch)) {
                if (ch == Constants.SYMBOLESCAPE) break
                if (ch == '\\') ch = _input!!.readNext()
                collect.append(ch)
            } else {
                throw lexError("unexpected end of escaped symbol " + ch)
            }
        }
        return _symbolTable!!.internSymbol(Symbol.Companion.symbolFactory(collect.toString(), true))
    }

    fun lexError(msg: String?): LexException {
        return LexException(msg, _input!!.getFilename(), _input!!.getLineNumber())
    }


    @kotlin.Throws(GenyrisException::class)
    fun parseIdent(): Exp? {
        var ch: Char
        val collect = StringBuffer("")
        if (!_input!!.hasData()) {
            throw lexError("unexpected end of file")
        }
        while (_input!!.hasData()) {
            ch = _input!!.readNext()
            if (isIdentCharacter(ch)) {
                if (ch == '\\') ch = _input!!.readNext()
                collect.append(ch)
            } else {
                _input!!.unGet(ch)
                break
            }
        }
        return replaceMacro(collect)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun replaceMacro(collect: StringBuffer): Exp? {
        //
        // replace @LINE and @FILE with current values or return a symbol.
        //
        if (collect.toString() == Constants.ATLINE) {
            return Bignum(this.lineNumber)
        } else if (collect.toString() == Constants.ATFILE) {
            return StrinG(_input!!.getFilename())
        } else {
            return _symbolTable!!.internSymbol(_mapper!!.symbolFactory(collect.toString()))
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun nextToken(): Exp? {
        var ch: Char
        do {
            if (!_input!!.hasData()) {
                return EOF_TOKEN
            }
            ch = _input!!.readNext()
            if (ch == this._cdrCharacter) return CDR_TOKEN

            if (ch == this._commentCharacter) {
                while (_input!!.hasData()) {
                    ch = _input!!.readNext()
                    if (ch == '\n') {
                        break
                    }
                }
            }
            if (ch == _dynamicCharacter) {
                return DYNAMIC_TOKEN
            }

            when (ch) {
                '\f', '\n', '\t', ' ', '\r' -> {}
                '-' -> {
                    if (!_input!!.hasData()) {
                        return EOF_TOKEN
                    }
                    ch = _input!!.readNext()
                    if (ch >= '0' && ch <= '9') {
                        _input!!.unGet(ch)
                        _input!!.unGet('-')
                        return parseNumber()
                    } else {
                        _input!!.unGet(ch)
                        _input!!.unGet('-')
                        return parseIdent()
                    }
                }

                '"' -> {
                    _input!!.unGet(ch)
                    return parseString('"')
                }

                '\'' -> {
                    _input!!.unGet(ch)
                    return parseString('\'')
                }

                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    _input!!.unGet(ch)
                    return parseNumber()
                }

                ';' -> return SEMI_TOKEN
                '!' -> return PLING_TOKEN
                '(' -> return LEFT_PAREN_TOKEN
                ')' -> return RIGHT_PAREN_TOKEN
                '[' -> return LEFT_SQUARE_TOKEN
                ']' -> return RIGHT_SQUARE_TOKEN
                '{' -> return LEFT_CURLY_TOKEN
                '}' -> return RIGHT_CURLY_TOKEN
                Constants.QUOTECHAR -> return QUOTE_TOKEN
                Constants.BQUOTECHAR -> return BACKQUOTE_TOKEN
                Constants.DOLLARCHAR -> {
                    if (_input!!.hasData()) {
                        ch = _input!!.readNext()
                        if (ch == Constants.ATCHAR) {
                            return DOLLAR_AT_TOKEN
                        } else {
                            _input!!.unGet(ch)
                            ch = Constants.DOLLARCHAR
                            return DOLLAR_TOKEN
                        }
                    } else {
                        return DOLLAR_TOKEN
                    }
                }

                Constants.SYMBOLESCAPE -> return parseIdentEscaped()
                else -> if ((ch >= ' ') && (ch <= '~')) {
                    _input!!.unGet(ch)
                    return parseIdent()
                } else {
                    throw lexError("invalid input character " + ch)
                }
            }
        } while (true)
    }

    @kotlin.Throws(LexException::class)
    fun parseString(quotechar: Char): Exp {
        var ch: Char
        val collect = StringBuffer()
        ch = _input!!.readNext()
        while (_input!!.hasData()) {
            ch = _input!!.readNext()
            if (ch == quotechar) {
                break
            } else {
                if (ch == '\\') {
                    if (!_input!!.hasData()) throw lexError("unexpected end of file")
                    val ch2 = _input!!.readNext()
                    when (ch2) {
                        'a' -> ch = '\u0007'
                        'n' -> ch = '\n'
                        'r' -> ch = '\r'
                        't' -> ch = '\t'
                        'f' -> ch = '\f'
                        '"' -> ch = '\"'
                        'e' -> ch = '\u001b'
                        '\\' -> ch = '\\'
                        else -> ch = ch2
                    }
                }
                collect.append(ch)
            }
        }
        return StrinG(collect, quotechar)
    }

    @kotlin.Throws(GenyrisException::class)
    fun addprefix(prefix: String, uri: String?) {
        _mapper!!.addAbbreviation(prefix, uri)
    }

    @kotlin.Throws(LexException::class)
    fun resetAfterError() {
        _input!!.resetAfterError()
    }

    fun withinExpression(env: Environment?) {
        _input!!.withinExpression(env)
    }

    fun beginningExpression() {
        _input!!.beginningExpression()
    }

    val lineNumber: Int
        get() = _input!!.getLineNumber()

    val filename: String?
        get() = _input!!.getFilename()
}
