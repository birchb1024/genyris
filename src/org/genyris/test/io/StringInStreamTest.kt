// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.io.StringInStream

class StringInStreamTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun testNormal() {
        val str1 = StringInStream("1234567")
        Assert.assertEquals('1', str1.readNext())
        Assert.assertEquals('2', str1.readNext())
        Assert.assertEquals('3', str1.readNext())
    }
}
