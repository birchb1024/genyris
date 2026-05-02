// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.core.Constants
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment

class IndentStream(var _instream: InStream, interactiveMode: Boolean) : InStreamEOF {
    var _parseState: Int

    var _tabs: IntArray?

    var _numberOfLeadingSpaces: Int = 0

    var _tabsToGo: Int = 0

    var _lineLevel: Int

    var _currentLevel: Int = 0 // current indent level (of previous line) -1 means

    // prior to first line
    var ch: Char = 0.toChar()

    private val _bufferit: IntArray // TODO maybe use a stringbuffer or some other

    // type
    var _bufferitReadPtr: Int = 0 // where are we up to.

    var _bufferitWritePtr: Int = 0 // where are we up to.

    var _interactive: Boolean
    private var _maxTab: Int
    private var _stringType = 0.toChar()
    private var _lineCount = 1
    private var _parenCount: Int

    init {
        _tabs = IntArray(MAX_TAB_DEPTH)
        _bufferit = IntArray(MAX_INDENTATION_BUFFER)
        _lineLevel = 0
        _parseState = LEADING_WHITE_SPACE
        _maxTab = 1
        _interactive = interactiveMode
        _parenCount = 0
    }

    @kotlin.Throws(LexException::class)
    fun unGet(x: Char) {
        throw lexError("unGet() not implemented in IndentStream!")
    }

    @kotlin.jvm.JvmOverloads
    fun trace(sign: String? = " ") {
//				System.out.printf("%s:%d %d %s %c %d\n",
//						this._instream.getFilename() ,
//						this._instream.getLineNumber(),
//						this._parseState,
//						sign,
//						ch,
//						_parenCount);
    }

    fun incrementParenCount() {
        _parenCount += 1
        trace("+")
    }

    fun decrementParenCount() {
        _parenCount -= 1
        trace("-")
    }

    @kotlin.Throws(LexException::class)
    fun checkParens() {
        if (_parenCount != 0) {
            startLine()
            throw lexError("unbalanced parentheses")
        }
    }

    fun lexError(message: String?): LexException {
        return LexException(message, _instream.getFilename(), _instream.getLineNumber() - 1)
    }

    @kotlin.Throws(LexException::class)
    fun startLine() {
        _lineLevel = 0
        _parseState = LEADING_WHITE_SPACE
        _numberOfLeadingSpaces = 0
        _lineCount++
    }

    @kotlin.Throws(LexException::class)
    fun input() {
        ch = _instream.readNext()
    }

    @kotlin.Throws(LexException::class)
    fun bufferit(c: Int) {
        bufferit(c, 1)
    }

    @kotlin.Throws(LexException::class)
    fun bufferit(ch: Int, num: Int) {
        var num = num
        while (num > 0) {
            if (_bufferitWritePtr > _bufferit.size) {
                throw lexError("lexer buffer overrun")
            }
            _bufferit[_bufferitWritePtr++] = ch
            num--
        }
    }

    fun bufferitEmpty(): Boolean {
        return _bufferitReadPtr >= _bufferitWritePtr
    }

    @kotlin.Throws(LexException::class)
    fun bufferitReadNext(): Int {
        if (bufferitEmpty()) throw lexError("Tried to read past end of buffer.")
        val result = _bufferit[_bufferitReadPtr++]
        if (bufferitEmpty()) {
            _bufferitWritePtr = 0
            _bufferitReadPtr = _bufferitWritePtr
        }
        return result
    }

    @kotlin.Throws(LexException::class)
    override fun resetAfterError() {
        startLine()
        _bufferitWritePtr = 0
        _bufferitReadPtr = _bufferitWritePtr
        _currentLevel = 0
        _instream.resetAfterError()
        _parenCount = 0
    }

    @kotlin.Throws(LexException::class)
    override fun getChar(): Int {
        while (true) {
            when (_parseState) {
                LEADING_WHITE_SPACE -> {
                    if (!_instream.hasData()) {
                        finish()
                        break
                    }
                    input()
                    trace()
                    if (ch == ' ') {
                        _numberOfLeadingSpaces++
                        break
                    } else if (ch == Constants.COMMENTCHAR) {
                        _parseState = STRIP_COMMENT
                        break
                    } else if (ch == '\r') {
                        break // probably MS-DOS line end - ignore
                    } else if (ch == '\n') {
                        if (_interactive) {
                            // finish the indentations all up, don't wait for more
                            // lines
                            _lineLevel = 0
                            _numberOfLeadingSpaces = 0
                            bufferit(')'.code, _currentLevel)
                            _currentLevel = 0
                            _parseState = NEXTLINE
                            removeTabsAfter(1)
                            _lineCount++
                            break
                        } else {
                            checkParens()
                            startLine()
                        }
                        break
                    } else if (ch == '\t') {
                        throw lexError(
                            ("illegal tab character before statement at line "
                                    + Integer.toString(getLineNUmber()) +
                                    " looking for new expresseion...")
                        )
                    } else if (ch == '~') {
                        // Continuation line so pretend indentation is aligned
                        // with the previous level
                        // Example:
                        // foo
                        //   bar 1 2
                        //   ~ '(1 2 3 4 5)
                        // Gives: (foo (bar 1 2) '(1 2 3 4 5))
                        // xyz
                        //   foo
                        //   bar 1 2
                        //   ~ quux
                        // Gives: (xyz (foo (bar 1 2)) quux)

                        stripLeadingSpaces()

                        // readAndBufferRestOfLine();
                        _lineLevel = computeDepthFromSpaces(_numberOfLeadingSpaces)

                        bufferit(')'.code, _currentLevel - _lineLevel + 1)
                        bufferit(' '.code)
                        _currentLevel = _lineLevel - 1
                        removeTabsAfter(_currentLevel)

                        _parseState = CATCHUP
                        break
                    } else {
                        if (_numberOfLeadingSpaces == 0) {
                            removeTabsAfter(1)
                        }
                        _lineLevel = computeDepthFromSpaces(_numberOfLeadingSpaces)

                        if (_currentLevel == _lineLevel) {
                            // Same indentation as previous line.
                            bufferit(')'.code)
                            bufferit('('.code)
                        } else if (_currentLevel < _lineLevel) {
                            bufferit('('.code, _lineLevel - _currentLevel)
                        } else if (_currentLevel > _lineLevel) {
                            bufferit(')'.code, _currentLevel - _lineLevel + 1)
                            bufferit('('.code)
                            removeTabsAfter(_lineLevel)
                        }
                        if (ch == '"' || ch == '\'') {
                            _instream.unGet(ch)
                        } else if (ch == '(') {
                            incrementParenCount()
                            bufferit(ch.code)
                        } else if (ch == ')') {
                            decrementParenCount()
                            bufferit(ch.code)
                        } else {
                            bufferit(ch.code)
                        }
                        _currentLevel = _lineLevel
                        _parseState = CATCHUP
                        break
                    }
                }

                IN_STRING_ESC -> {
                    if (!_instream.hasData()) {
                        finish()
                        break
                    }
                    input()
                    _parseState = IN_STRING
                    return (ch).code
                }

                IN_SYMBOL -> {
                    if (!_instream.hasData()) {
                        checkParens()
                        finish()
                        break
                    }
                    input()

                    when (ch) {
                        '|' -> {
                            _parseState = IN_STATEMENT
                            return (ch).code
                        }

                        '\n' -> {
                            checkParens()
                            startLine()
                        }

                        else -> return (ch).code
                    }

                    if (!_instream.hasData()) {
                        checkParens()
                        finish()
                        break
                    }
                    input()
                    if (ch == _stringType) {
                        _parseState = IN_STATEMENT
                        return (ch).code
                    }
                    when (ch) {
                        '\\' -> {
                            _parseState = IN_STRING_ESC
                            return (ch).code
                        }

                        else -> return (ch).code
                    }
                }

                IN_STRING -> {
                    if (!_instream.hasData()) {
                        checkParens()
                        finish()
                        break
                    }
                    input()
                    if (ch == _stringType) {
                        _parseState = IN_STATEMENT
                        return (ch).code
                    }
                    when (ch) {
                        '\\' -> {
                            _parseState = IN_STRING_ESC
                            return (ch).code
                        }

                        else -> return (ch).code
                    }
                }

                IN_STATEMENT -> {
                    if (!_instream.hasData()) {
                        checkParens()
                        finish()
                        break
                    }
                    input()
                    trace()
                    when (ch) {
                        '\'', '"' -> {
                            _parseState = IN_STRING
                            _stringType = ch
                            return (ch).code
                        }

                        '|' -> {
                            _parseState = IN_SYMBOL
                            return (ch).code
                        }

                        Constants.COMMENTCHAR -> _parseState = STRIP_COMMENT
                        '\n' -> {
                            checkParens()
                            startLine()
                        }

                        '(' -> {
                            incrementParenCount()
                            return (ch).code
                        }

                        ')' -> {
                            decrementParenCount()
                            return (ch).code
                        }

                        else -> return (ch).code
                    }
                }

                STRIP_COMMENT -> if (_instream.hasData()) {
                    input()
                    if (ch == '\n') {
                        checkParens()
                        startLine()
                        break
                    }
                } else finish()

                CATCHUP -> if (bufferitEmpty()) {
                    _parseState = IN_STATEMENT
                } else {
                    val result = bufferitReadNext()
                    if (bufferitEmpty() && result == Constants.SYMBOLESCAPE.code) {
                        _parseState = IN_SYMBOL
                    }
                    return result
                }

                NEXTLINE -> if (bufferitEmpty()) {
                    startLine()
                } else {
                    val result = bufferitReadNext()
                    return result
                }

                FINISHING -> if (bufferitEmpty()) {
                    return InStreamEOF.Companion.EOF
                } else {
                    val result = bufferitReadNext()
                    return result
                }
            }
        }
    }

    @kotlin.Throws(LexException::class)
    private fun stripLeadingSpaces() {
        while (_instream.hasData()) {
            input()
            if (ch != ' ') {
                _instream.unGet(ch)
                break
            }
        }
    }

    @kotlin.Throws(LexException::class)
    private fun finish() {
        // close all parenthesis

        bufferit(')'.code, _currentLevel)
        _parseState = FINISHING
    }

    @kotlin.Throws(LexException::class)
    fun hasData(): Boolean {
        throw lexError("hasData() not implemented")
    }

    fun removeTabsAfter(newMax: Int) {
        _maxTab = newMax // TODO unnecessary - done below alos ?
    }

    @kotlin.Throws(LexException::class)
    fun computeDepthFromSpaces(numsp: Int): Int {
        if (numsp == 0) {
            return (1)
        }
        if (numsp > _tabs!![_maxTab - 1]) {
            if (_maxTab > _tabs!!.size) {
                throw lexError("input stream indented too deeply")
            }
            _tabs!![_maxTab] = numsp // remember the tabstop
            _maxTab++
            return (_maxTab)
        } else {
            // look for the first tabstop on the left, starting at the left
            // margin
            for (i in 0..<_maxTab) { // sequential search
                // is esential here. (left to right)

                if (_tabs!![i] == numsp) {
                    _maxTab = i + 1
                    return (_maxTab)
                }
            }
            // nothing matching, so it's an error
            throw lexError(
                "invalid indentation not matching previous indentation"
            )
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun close() {
        _instream.close()
    }

    override fun withinExpression(env: Environment?) {
        this._instream.withinExpression(env)
    }

    override fun beginningExpression() {
        this._instream.beginningExpression()
    }

    override fun getLineNUmber(): Int {
        if (bufferitEmpty()) {
            return _lineCount
        } else {
            return _lineCount - 1
        }
    }

    override fun getFilename(): String? {
        return _instream.getFilename()
    }

    companion object {
        private const val MAX_TAB_DEPTH = 256

        private const val MAX_INDENTATION_BUFFER = 2048

        private const val LEADING_WHITE_SPACE = 0

        private const val IN_STATEMENT = 1

        private const val CATCHUP = 2

        private const val FINISHING = 3

        private const val IN_STRING = 4

        private const val IN_STRING_ESC = 5

        private const val STRIP_COMMENT = 6

        private const val NEXTLINE = 7

        private const val IN_SYMBOL = 8
    }
}
