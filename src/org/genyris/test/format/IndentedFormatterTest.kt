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
import org.genyris.io.InStream
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import java.io.StringWriter

class IndentedFormatterTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun excerciseFormatter(given: String, expected: String?, depth: Int) {
        val interpreter = Interpreter()
        val input: InStream = UngettableInStream(StringInStream(given))
        val parser = interpreter.newParser(input)
        val expression = parser.read()
        val out = StringWriter()
        val formatter: Formatter = IndentedFormatter(out, depth)

        expression.acceptVisitor(formatter)

        Assert.assertEquals(expected, out.getBuffer().toString())
    }

    @kotlin.Throws(Exception::class)
    fun testAtom1() {
        excerciseFormatter("12", "~ 12", 2)
    }

    @kotlin.Throws(Exception::class)
    fun testAtom2() {
        excerciseFormatter("nil", "~ nil", 2)
    }

    @kotlin.Throws(Exception::class)
    fun testAtom3() {
        excerciseFormatter("1.23E15", "~ 1230000000000000", 2)
    }

    @kotlin.Throws(Exception::class)
    fun testAtom4() {
        excerciseFormatter("foo", "~ foo", 2)
    }

    @kotlin.Throws(Exception::class)
    fun testAtom5() {
        excerciseFormatter("'string'", "~ 'string'", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test1() {
        excerciseFormatter("(1 2.3 'str' (symbol))", "1 2.3 'str'\n   symbol", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test11() {
        excerciseFormatter("(1 (symbol) (sym2) (sym3))", "1 (symbol) (sym2)\n   sym3", 3)
    }

    @kotlin.Throws(Exception::class)
    fun test2() {
        excerciseFormatter("(1 2.3 (nil nil 23 45 = 89))", "1 2.3\n   nil nil 23 45 = 89", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test3() {
        excerciseFormatter("(45 = 89)", "45 = 89", 3)
    }

    @kotlin.Throws(Exception::class)
    fun test4() {
        excerciseFormatter("(1 2.3 'str' (symbol))", "1 2.3 'str' (symbol)", 4)
    }

    @kotlin.Throws(Exception::class)
    fun test5() {
        excerciseFormatter("(1 2 3 4 (23 45))", "1 2 3 4\n   23 45", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test6() {
        excerciseFormatter("(1 (22) 3)", "1 (22) 3", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test7() {
        excerciseFormatter("(1 2 3 (44) 5)", "1 2 3\n   44\n   ~ 5", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test8() {
        excerciseFormatter("(1 2 3 (44) 5 (66) 7 (88) 9)", "1 2 3\n   44\n   ~ 5\n   66\n   ~ 7\n   88\n   ~ 9", 2)
    }

    @kotlin.Throws(Exception::class)
    fun test9() {
        excerciseFormatter("(xxx (a) (b : 2))", "xxx (a) (b : 2)", 3)
        excerciseFormatter("(xxx (a) (b : 2))", "xxx (a) (b : 2)", 3)
        excerciseFormatter("(xxx (a : 1) (b : 2))", "xxx (a : 1) (b : 2)", 3)
        excerciseFormatter("(xxx (a : (1)))", "xxx (a : (1))", 3)
    }

    @kotlin.Throws(Exception::class)
    fun test10() {
        excerciseFormatter("(a w (k (3 4)))", "a w\n   k\n      3 4", 1)
        excerciseFormatter("(a w ((1 2) (3 4)))", "a w\n   (1 2)\n      3 4", 1)
    }
}
