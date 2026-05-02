// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.io.LexException
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream

class UngettableInStreamTest : TestCase() {
    @kotlin.Throws(LexException::class)
    fun testUngettable() {
        val ung = UngettableInStream(StringInStream("123"))

        ung.unGet('a')
        Assert.assertEquals('a', ung.readNext())
        Assert.assertEquals('1', ung.readNext())
        ung.unGet('b')
        ung.unGet('c')
        ung.unGet('d')
        Assert.assertEquals('d', ung.readNext())
        Assert.assertEquals('c', ung.readNext())
        Assert.assertEquals('b', ung.readNext())
        Assert.assertEquals('2', ung.readNext())
        Assert.assertEquals('3', ung.readNext())
    }
}
