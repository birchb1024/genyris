// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.format

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.format.Formatter
import org.genyris.format.IndentedFormatter
import org.genyris.interp.Interpreter
import org.genyris.io.*
import java.io.StringWriter

class RoundtripIndentedFormatterTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun excerciseFormatter(given: String) {
        // Parse the test statement to a Lisin expression

        val interpreter = Interpreter()
        val input: InStream = UngettableInStream(StringInStream(given))
        val parser = interpreter.newParser(input)
        val expression = parser.read()
        // Output as an indented string
        val out = StringWriter()
        val formatter: Formatter = IndentedFormatter(out, 3)
        expression.acceptVisitor(formatter)
        val formatted = out.getBuffer().toString()
        // Parse the output back into Lisp again
        val toParseAgain: InStream =
            UngettableInStream(ConvertEofInStream(IndentStream(UngettableInStream(StringInStream(formatted)), false)))
        val parser2 = interpreter.newParser(toParseAgain)
        val expression2 = parser2.read()

        // The re-read expresion and the original should be the same
        Assert.assertEquals(expression.toString(), expression2.toString())
    }

    @kotlin.Throws(Exception::class)
    fun testRountrip2() {
        excerciseFormatter("(12)")
    }

    @kotlin.Throws(Exception::class)
    fun testRountrip3() {
        excerciseFormatter("(symbol \"string\" 1 2.456 (list a b c d) 4)")
    }

    @kotlin.Throws(Exception::class)
    fun testRountrip4() {
        excerciseFormatter("(symbol \"string\" 1 45 (list a b (symbol \"string\" 1 45 (list a b c d) 4) d) 4)")
    }

    @kotlin.Throws(Exception::class)
    fun testFrame1() {
        excerciseFormatter("(new (a 1) (b 2) (c 3) (d 4))")
    }
}
