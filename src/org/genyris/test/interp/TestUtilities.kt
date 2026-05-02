// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.format.BasicFormatter
import org.genyris.format.Formatter
import org.genyris.interp.Interpreter
import org.genyris.io.Parser
import java.io.StringWriter

class TestUtilities {
    var _interpreter: Interpreter

    init {
        _interpreter = Interpreter()
        _interpreter.init(false, "TestUtilities")
    }


    @kotlin.Throws(GenyrisException::class)
    fun eval(script: String?): String {
        val expression: Exp = Parser.Companion.parseSingleExpressionFromString(_interpreter.getSymbolTable(), script)
        val result = _interpreter.evalInGlobalEnvironment(expression)

        return renderExp(result, false)
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun renderExp(X: Exp, expandAbbreviation: Boolean): String {
            val out = StringWriter()
            val formatter: Formatter = BasicFormatter(out, expandAbbreviation)
            X.acceptVisitor(formatter)
            return out.getBuffer().toString()
        }
    }
}
