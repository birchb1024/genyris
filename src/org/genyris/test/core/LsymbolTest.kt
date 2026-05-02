// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.PrefixSymbol
import org.genyris.core.SimpleSymbol
import org.genyris.core.Symbol

class LsymbolTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun testSetGet1() {
        var sym = SimpleSymbol("G0")
        Assert.assertEquals("G0", sym.getPrintName())



        sym = SimpleSymbol("G1")
        Assert.assertEquals("G1", sym.getPrintName())

        sym = Symbol.Companion.symbolFactory("http://foo.bvar/quux", false)
        Assert.assertEquals("http://foo.bvar/quux", sym.getPrintName())
        Assert.assertEquals("|http://foo.bvar/quux|", sym.toString())


        sym = PrefixSymbol("quux", "wobble", "http://foo.bvar/quux/")
    }
}
