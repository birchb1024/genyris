// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.format

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.format.Formatter
import org.genyris.format.JSONFormatter
import org.genyris.interp.Interpreter
import org.genyris.io.InStream
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import java.io.StringWriter

class JSONFormatterTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun excerciseFormatter(given: String, expected: String?) {
        val interpreter = Interpreter()
        val input: InStream = UngettableInStream(StringInStream(given))
        val parser = interpreter.newParser(input)
        val expression = parser.read()
        val computed = interpreter.evalInGlobalEnvironment(expression)
        val out = StringWriter()
        val formatter: Formatter = JSONFormatter(out)

        computed.acceptVisitor(formatter)

        Assert.assertEquals(expected, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    fun test1() {
        excerciseFormatter("^(1 2.3 'str' (symbol))", "[ 1 , 2.3 , \"str\" , [ \"symbol\" ] ]")
    }

    @kotlin.Throws(Exception::class)
    fun test2() {
        excerciseFormatter("^(1 2.3 (nil nil 2e3 45 89))", "[ 1 , 2.3 , [ [] , [] , 2000 , 45 , 89 ] ]")
    }

    @kotlin.Throws(Exception::class)
    fun test3() {
        excerciseFormatter("^(45 89)", "[ 45 , 89 ]")
    }

    @kotlin.Throws(Exception::class)
    fun testcdr() {
        excerciseFormatter("^(45 = 89)", "[ 45 , 89 ]")
    }

    @kotlin.Throws(Exception::class)
    fun testSymbolsStrange() {
        excerciseFormatter(
            "^(|foo| |\\\" | .|jhg| )",
            "[ \"foo\" , \"\\\" \" , \"jhg\" ]"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testSymbols() {
        excerciseFormatter(
            "^(quux |fo:o| |http://foo/bar#| .|http://www.genyris.org/lang/system#foo|)",
            "[ \"quux\" , \"fo:o\" , \"http://foo/bar#\" , \"http://www.genyris.org/lang/system#foo\" ]"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testDictionary() {
        excerciseFormatter(
            "(dict (.a = 1) (.b = 'b'))",
            "{ \"a\" : 1 , \"b\" : \"b\" }"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testComplex() {
        excerciseFormatter(
            "(dict (.a = ^(1 2 3)) (.b = 'b') (.c = (dict (.d = 'D'))))",
            "{ \"a\" : [ 1 , 2 , 3 ] , \"b\" : \"b\" , \"c\" : { \"d\" : \"D\" } }"
        )
    }
}
