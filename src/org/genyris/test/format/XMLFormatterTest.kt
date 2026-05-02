// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.format

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.format.Formatter
import org.genyris.format.HTMLFormatter
import org.genyris.interp.Interpreter
import org.genyris.io.InStream
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import java.io.StringWriter

class XMLFormatterTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun excerciseFormatter(given: String, expected: String?) {
        val interpreter = Interpreter()
        val input: InStream = UngettableInStream(StringInStream(given))
        val parser = interpreter.newParser(input)
        val expression = parser.read()
        val out = StringWriter()
        val formatter: Formatter = HTMLFormatter(out)

        expression.acceptVisitor(formatter)

        Assert.assertEquals(expected, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    fun test1() {
        excerciseFormatter("(1 2.3 'str' (symbol))", "12.3str<symbol/>")
    }

    @kotlin.Throws(Exception::class)
    fun test2() {
        excerciseFormatter("(1 2.3 (nil nil 23 45 89))", "12.3<nil>234589</nil>")
    }

    @kotlin.Throws(Exception::class)
    fun test3() {
        excerciseFormatter("(45 89)", "4589")
    }

    @kotlin.Throws(Exception::class)
    fun testSymbols() {
        excerciseFormatter(
            "(quux |fo:o| |http://foo/bar#| .|http://www.genyris.org/lang/system#foo|)",
            "<quux *** error bad HTML attribute: |fo:o|><http://foo/bar# *** error bad HTML attribute: .|http://www.genyris.org/lang/system#foo|/></quux>"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testPage() {
        excerciseFormatter(
            "(html((base='foo')(quux='bar'))(body()'text'(img((src='http://foo/')))))",
            "<html base=\"foo\" quux=\"bar\"><body>text<img src=\"http://foo/\"/></body></html>"
        )
        excerciseFormatter("(verbatim() '&nbsp;<ww><www></ww>')", "&nbsp;<ww><www></ww>")
    }
}
