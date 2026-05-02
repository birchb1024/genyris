// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.*
import org.genyris.exception.AccessException

class CoreTest : TestCase() {
    private var NIL: SimpleSymbol? = null

    public override fun setUp() {
        NIL = NilSymbol()
    }

    fun testAccessExceptionCar() {
        val a: Exp = Bignum(0)
        try {
            a.car()
            fail("expecting exception")
        } catch (e: AccessException) {
        }
    }

    fun testAccessExceptionCdr() {
        val a: Exp = Bignum(0)
        try {
            a.cdr()
            fail("expecting exception")
        } catch (e: AccessException) {
        }
    }

    fun testAccessExceptionSetCar() {
        val a: Exp = Bignum(0)
        try {
            a.setCar(SimpleSymbol("foo"))
            fail("expecting exception")
        } catch (e: AccessException) {
        }
    }

    fun testAccessExceptionSetCdr() {
        val a: Exp = Bignum(0)
        try {
            a.setCar(SimpleSymbol("foo"))
            fail("expecting exception")
        } catch (e: AccessException) {
        }
    }

    @kotlin.Throws(AccessException::class)
    fun testLength() {
        val list: Exp = Pair(Bignum(1), Pair(Bignum(2), NIL))
        Assert.assertEquals(2, list.length(NIL))
        Assert.assertEquals(1, list.cdr().length(NIL))
        Assert.assertEquals(0, list.cdr().cdr().length(NIL))
    }

    @kotlin.Throws(AccessException::class)
    fun testLengthNIL() {
        val list: Exp = NIL!!
        try {
            list.cdr().cdr().cdr().length(NIL)
            fail("expecting exception")
        } catch (e: AccessException) {
        } finally {
        }
    }

    fun testLisp() {
        assertTrue(Pair(NIL, NIL).isPair())
        assertFalse(Bignum(1).isPair())
    }
}
