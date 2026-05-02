// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core

import junit.framework.TestCase
import org.genyris.core.*
import org.genyris.interp.Interpreter
import org.genyris.interp.StandardEnvironment
import java.lang.Double
import kotlin.Any
import kotlin.Exception
import kotlin.collections.HashMap
import kotlin.collections.MutableMap

class EqualityTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun testInt1() {
        assertTrue(Bignum(12) == Bignum(12))
        assertFalse(Bignum(11) == Bignum(12))
    }

    @kotlin.Throws(Exception::class)
    fun testDouble1() {
        assertTrue(Bignum(12.23e9) == Bignum(12.23e9))
        assertFalse(Bignum(12.230001e9) == Bignum(12.23e9))
    }

    @kotlin.Throws(Exception::class)
    fun testString() {
        assertTrue(StrinG("hello") == StrinG("hello"))
        assertFalse(StrinG("hello1") == StrinG("hello2"))
    }

    @kotlin.Throws(Exception::class)
    fun testCons() {
        assertTrue(Pair(StrinG("hello"), Bignum(12)) == Pair(StrinG("hello"), Bignum(12)))
        assertFalse(Pair(StrinG("hello"), StrinG("no way")) == Pair(StrinG("hello"), Bignum(12)))
    }

    @kotlin.Throws(Exception::class)
    fun testPairEqual() {
        assertTrue(Pair(StrinG("hello"), Bignum(12)) == PairEquals(StrinG("hello"), Bignum(12)))
        assertFalse(Pair(StrinG("hello"), StrinG("no way")) == PairEquals(StrinG("hello"), Bignum(12)))
    }

    @kotlin.Throws(Exception::class)
    fun testSymbol() {
        val sym = SymbolTable()
        sym.init(NilSymbol())
        assertFalse(SimpleSymbol("hello1") == SimpleSymbol("hello2"))
        assertTrue(SimpleSymbol("hello") == SimpleSymbol("hello"))
        assertTrue(sym.internString("hello") == sym.internString("hello"))
        assertTrue(sym.internString("hello") == SimpleSymbol("hello"))
    }

    @kotlin.Throws(Exception::class)
    fun testHashMap() {
        val dict1: MutableMap<*, *> = HashMap<Any?, Any?>()
        dict1.put(Double.valueOf(123.345), Integer.valueOf(2))
        val dict2: MutableMap<*, *> = HashMap<Any?, Any?>()
        dict2.put(Double.valueOf(123.345), Integer.valueOf(2))
        assertTrue("foo" == "foo")
        assertTrue(dict1 == dict2)
    }

    @kotlin.Throws(Exception::class)
    fun testFrame() {
        val interp = Interpreter()
        val a = SimpleSymbol("a")
        val f1 = Dictionary(StandardEnvironment(interp.getSymbolTable(), NilSymbol()))
        f1.defineVariableRaw(a, StrinG("foo"))
        val f2 = Dictionary(StandardEnvironment(interp.getSymbolTable(), NilSymbol()))
        f2.defineVariableRaw(a, StrinG("foo"))
        assertTrue(f1 == f2)
    }
}
