package org.genyris.io

import jline.console.ConsoleReader
import jline.console.completer.ArgumentCompleter
import jline.console.completer.ArgumentCompleter.AbstractArgumentDelimiter
import jline.console.completer.StringsCompleter
import org.apache.commons.io.output.WriterOutputStream
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.io.IOException
import java.io.Reader

class JlineStdioInStream private constructor() : InStream {
    //
    // WARNING - this class has only one instance shared between all threads.
    //
    private var _nextIndex = 0
    private val _jline: ConsoleReader? = null
    private var _nextLine: String? = null
    private var _completer: ArgumentCompleter? = null
    private var _environment: Environment? = null
    private var _lineCount = 0

    @kotlin.Throws(UnsupportedOperationException::class)
    override fun getReader(): Reader? {
        throw UnsupportedOperationException()
    }

    init {
        try {
            _jline = ConsoleReader()
            _jline.setPrompt("> ")
            _jline.setExpandEvents(false)
            _completer = null
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    val output: OutputStream
        get() = WriterOutputStream(_jline!!.getOutput())

    @kotlin.jvm.Synchronized
    @kotlin.Throws(LexException::class)
    override fun unGet(x: Char) {
        throw LexException("StdioStream: unGet not implemented.")
    }

    @kotlin.jvm.Synchronized
    @kotlin.Throws(LexException::class)
    override fun readNext(): Char {
        if (_nextLine == null) {
            throw LexException(
                "StdioInStream: readNext() called before hasData()"
            )
        }
        val retval: Int = _nextLine.charAt(_nextIndex).code
        _nextIndex++
        if (_nextLine.length() == _nextIndex) {
            // read the last one
            _nextLine = null
            _nextIndex = 0
        }
        return retval.toChar()
    }

    @kotlin.jvm.Synchronized
    override fun hasData(): Boolean {
        try {
            if (_nextLine != null) {
                return true
            }
            _nextLine = _jline!!.readLine()
            _lineCount++
            _nextIndex = 0
        } catch (e: IOException) {
            _nextLine = null
            return false
        }
        if (_nextLine == null) {
            return false
        } else {
            _nextLine = _nextLine + '\n'
            return true
        }
    }

    private fun refreshTabCompletion() {
        if (_environment == null) {
            _environment = _interp!!.getGlobalEnv()
        }
        _jline!!.removeCompleter(_completer)
        _completer = ArgumentCompleter(
            GenyrisArgumentDelimiter(),
            StringsCompleter(
                _interp!!.getBoundSymbolsAsListOfStrings(_environment)
            )
        )
        _completer!!.setStrict(false)
        _jline.addCompleter(_completer)
    }

    @kotlin.jvm.Synchronized
    @kotlin.Throws(GenyrisException::class)
    override fun close() {
    }

    @kotlin.jvm.Synchronized
    override fun resetAfterError() {
        _nextLine = null
        beginningExpression()
    }

    fun setInterpreter(_interpreter: Interpreter) {
        _interp = _interpreter
        _environment = _interp!!.getGlobalEnv()
        refreshTabCompletion()
    }

    fun setEnvironment(env: Environment?) {
        _environment = env
        refreshTabCompletion()
    }

    override fun withinExpression(env: Environment?) {
        _environment = env
        refreshTabCompletion()
        _jline!!.setPrompt(": ")
    }

    override fun beginningExpression() {
        refreshTabCompletion()
        _jline!!.setPrompt("> ")
    }

    class GenyrisArgumentDelimiter : AbstractArgumentDelimiter() {
        override fun isDelimiterChar(buffer: CharSequence, pos: Int): Boolean {
            val ch: Char = buffer.charAt(pos)
            return Character.isWhitespace(ch) || ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == ',' || ch == '\'' || ch == '"' || ch == '#' || ch == '^' || ch == '{' || ch == '}'
        }
    }

    override fun getLineNumber(): Int {
        return _lineCount
    }

    override fun getFilename(): String {
        return "console"
    }

    companion object {
        private var singleton: JlineStdioInStream? = null
        private var _interp: Interpreter? = null

        @kotlin.jvm.Synchronized
        fun knew(): JlineStdioInStream { // the 'k' is silent.
            if (singleton == null) {
                singleton = JlineStdioInStream()
            }
            return singleton!!
        }
    }
}
