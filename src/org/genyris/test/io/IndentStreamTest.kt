// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.io.*

class IndentStreamTest : TestCase() {
    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    @kotlin.Throws(LexException::class)
    private fun excerciseIndent(toparse: String, expected: String?) {
        val ind: InStream = ConvertEofInStream(
            IndentStream(
                UngettableInStream(
                    StringInStream(toparse)
                ), false
            )
        )
        val result = StringBuffer()
        while (ind.hasData()) {
            result.append(ind.readNext())
        }
        Assert.assertEquals(expected, result.toString())

        // TODO DRY
    }

    @kotlin.Throws(LexException::class)
    private fun excerciseIndentInteractive(toparse: String, expected: String?) {
        val ind: InStream = ConvertEofInStream(
            IndentStream(
                UngettableInStream(
                    StringInStream(toparse)
                ), true
            )
        )
        val result = StringBuffer()
        while (ind.hasData()) {
            result.append(ind.readNext())
        }
        Assert.assertEquals(expected, result.toString())
    }

    @kotlin.Throws(LexException::class)
    fun testIndentCalc1() {
        val ind = IndentStream(StringInStream("123"), false)
        Assert.assertEquals(1, ind.computeDepthFromSpaces(0))
        Assert.assertEquals(2, ind.computeDepthFromSpaces(1))
        Assert.assertEquals(3, ind.computeDepthFromSpaces(2))
        Assert.assertEquals(3, ind.computeDepthFromSpaces(2))
        Assert.assertEquals(3, ind.computeDepthFromSpaces(2))
    }

    @kotlin.Throws(LexException::class)
    fun testIndentCalc2() {
        val ind = IndentStream(StringInStream("123"), false)
        Assert.assertEquals(1, ind.computeDepthFromSpaces(0))
        Assert.assertEquals(2, ind.computeDepthFromSpaces(1))
        Assert.assertEquals(2, ind.computeDepthFromSpaces(1))
        Assert.assertEquals(2, ind.computeDepthFromSpaces(1))
        Assert.assertEquals(2, ind.computeDepthFromSpaces(1))
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream1() {
        excerciseIndent("0\n 1", "(0(1))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream2() {
        excerciseIndent(
            "0 0 0 #\n 1 1 1#\n  2 2 2#\n 1 1 1#\n0 0 0",
            "(0 0 0 (1 1 1(2 2 2))(1 1 1))(0 0 0)"
        )
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream3() {
        excerciseIndent("0#\n 1#\n  2#\n 1#\n0", "(0(1(2))(1))(0)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream4() {
        excerciseIndent("0#\n 1#\n 1", "(0(1)(1))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream5() {
        excerciseIndent("0#\n 1#\n  2#\n 1#\n0", "(0(1(2))(1))(0)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream6() {
        excerciseIndent("0\n 1\n  2\n 1\n0", "(0(1(2))(1))(0)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream7() {
        excerciseIndent("0\n0\n", "(0)(0)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream8() {
        excerciseIndent("0\n 1\n 1", "(0(1)(1))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream9() {
        excerciseIndent("0#c\n 1#3\n#4", "(0(1))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream10() {
        excerciseIndent("0", "(0)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream11() {
        excerciseIndent("#comment", "")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream12() {
        excerciseIndent("", "")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream13() {
        excerciseIndent("     #comment", "")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream14() {
        excerciseIndent("0000", "(0000)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream15() {
        excerciseIndent("0 1 # 3 4", "(0 1 )")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream15a() {
        excerciseIndent("0 1 |23#45|", "(0 1 |23#45|)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream15b() {
        excerciseIndent("|http://foo/23#45|", "(|http://foo/23#45|)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream16() {
        excerciseIndent("0 1 2 3 4", "(0 1 2 3 4)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream17() {
        excerciseIndent("\n0000", "(0000)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream18() {
        excerciseIndentInteractive("1\n 2\n  3\n\n34", "(1(2(3)))(34)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream19() {
        excerciseIndentInteractive("1\n 2:\n  3", "(1(2:(3)))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream21() {
        excerciseIndentInteractive("1\n 2\n  3\n ~ 22", "(1(2(3)) 22)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream22() {
        excerciseIndentInteractive("~3", " 3")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream23() {
        excerciseIndentInteractive("~(3)", " (3)")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream25() {
        excerciseIndentInteractive("~1\n~2\n~3", " 1 2 3")
        // ~1
        // ~2
        // ~3
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream26() {
        excerciseIndentInteractive("foo\n bar\n ~1\n ~2\n~3", "(foo(bar) 1 2) 3")
        // foo
        // bar
        // ~1
        // ~2
        // ~3
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream27() {
        excerciseIndent(
            "defvar ^fn\n  lambda (x)\n    cond\n      (eq nil (cdr x))\n         x\n      else\n         fn (cdr x)\n",
            "(defvar ^fn(lambda (x)(cond((eq nil (cdr x))(x))(else(fn (cdr x))))))"
        )
    }

    @kotlin.Throws(LexException::class)
    fun testIndentStream28() {
        excerciseIndent("\"foo\"", "(\"foo\")")
        excerciseIndent("quote\n  \"string\"\n", "(quote(\"string\"))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentSplitStatement() {
        excerciseIndent("12\n  34\n\n  56", "(12(34)(56))")
        excerciseIndent("12\n  34\n  \n  56", "(12(34)(56))")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentMultiStatement() {
        excerciseIndent("12\n34", "(12)(34)")
        excerciseIndent("12\n34\n56", "(12)(34)(56)")
        excerciseIndent("12\n 34\n56", "(12(34))(56)")
        excerciseIndent("12\n 34\n\n56", "(12(34))(56)")
        excerciseIndent("12\n 34\n ~ 56\n78", "(12(34) 56)(78)")
        excerciseIndent("12\n 34\n\n~56", "(12(34)) 56")
    }

    @kotlin.Throws(LexException::class)
    fun testIndentMultiStatementWithSpaces() {
        excerciseIndent(
            "defvar ^Object\n   dict\n      .name ^Object\n    \n \ndefmacro class (name supers) \n foo\n",
            "(defvar ^Object(dict(.name ^Object)))(defmacro class (name supers) (foo))"
        )
    }
}
