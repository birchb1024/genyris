// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.format

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.format.BasicFormatter
import org.genyris.format.Formatter
import org.genyris.interp.Interpreter
import org.genyris.io.InStream
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import java.io.StringWriter

class BasicFormatterTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun excerciseFormatter(given: String) {
        val interpreter = Interpreter()
        val input: InStream = UngettableInStream(StringInStream(given))
        val parser = interpreter.newParser(input)
        val expression = parser.read()
        val out = StringWriter()
        val formatter: Formatter = BasicFormatter(out)

        expression.acceptVisitor(formatter)

        Assert.assertEquals(given, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    fun test1() {
        excerciseFormatter("(1 2.3 'str' (symbol))")
    }

    @kotlin.Throws(Exception::class)
    fun test2() {
        excerciseFormatter("(1 2.3 (nil nil 23 45 : 89))")
    }

    @kotlin.Throws(Exception::class)
    fun test3() {
        excerciseFormatter("(45 : 89)")
    }

    @kotlin.Throws(Exception::class)
    fun testSymbols() {
        excerciseFormatter("(quux |fo:o| |http://foo/bar#| .|http://www.genyris.org/lang/system#foo|)")
    }
}
