// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.Bignum
import org.genyris.core.Dictionary
import org.genyris.core.NilSymbol
import org.genyris.interp.Interpreter
import org.genyris.interp.StandardEnvironment

class DictTest : TestCase() {
    private var _frame: Dictionary? = null
    private var _interpreter: Interpreter? = null

    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        _interpreter = Interpreter()
        _frame = Dictionary(StandardEnvironment(_interpreter!!.getSymbolTable(), NilSymbol()))
    }

    @kotlin.Throws(Exception::class)
    fun test1() {
        val a = _interpreter!!.intern("a")
        Assert.assertEquals(false, _frame!!.hasKey(a))
        _frame!!.defineVariableRaw(a, Bignum(12))
        Assert.assertEquals(true, _frame!!.hasKey(a))
    }
}
