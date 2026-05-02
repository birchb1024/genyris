// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core

import junit.framework.TestCase
import org.genyris.core.Exp
import org.genyris.core.SymbolTable


class SymbolTableTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun testSymbolTable() {
        val tab = SymbolTable()
        tab.init(null)
        val foo1: Exp? = tab.internString("foo")
        val foo2: Exp? = tab.internString("foo")
        assertEquals(foo1, foo2)
    }
}
