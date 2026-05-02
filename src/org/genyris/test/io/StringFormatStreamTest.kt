// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.io.*

class StringFormatStreamTest : TestCase() {
    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    @kotlin.Throws(LexException::class)
    private fun excerciseSFS(toparse: String, expected: String?) {
        val ind: InStream = ConvertEofInStream(
            StringFormatStream(
                UngettableInStream(
                    StringInStream(toparse)
                )
            )
        )
        val result = StringBuffer()
        while (ind.hasData()) {
            result.append(ind.readNext())
        }
        Assert.assertEquals(expected, result.toString())
    }


    @kotlin.Throws(LexException::class)
    fun testSFStreamEmpty() {
        excerciseSFS("", "\"\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream2() {
        excerciseSFS("1", "\"1\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream3() {
        excerciseSFS("1", "\"1\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream4() {
        excerciseSFS("12343567890", "\"12343567890\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream5() {
        excerciseSFS("#{}", "\"\"\"\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream6() {
        excerciseSFS("#{alpha}", "\"\"alpha\"\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream7() {
        excerciseSFS("one#{alpha}two", "\"one\"alpha\"two\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStream8() {
        excerciseSFS("on\"e#{alpha}two", "\"on\\\"e\"alpha\"two\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStreamReal() {
        excerciseSFS("<img src=\"#{image-url-var}\">", "\"<img src=\\\"\"image-url-var\"\\\">\"")
    }

    @kotlin.Throws(LexException::class)
    fun testSFStreamMultiple() {
        excerciseSFS("12#{3}435#{6}78#{9}0", "\"12\"3\"435\"6\"78\"9\"0\"")
    }
}
