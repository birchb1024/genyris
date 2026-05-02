// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class Bignum : Atom, Comparable<Any?> {
    private val _value: BigDecimal

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.BIGNUM()
    }

    constructor(i: BigDecimal) {
        _value = i
    }

    constructor(i: Int) {
        _value = BigDecimal(i)
    }

    constructor(d: Double) {
        _value = BigDecimal(d)
    }

    constructor(string: String) {
        _value = BigDecimal(string)
    }

    constructor(i: Int) {
        _value = BigDecimal(i)
    }

    constructor(i: Long) {
        _value = BigDecimal(i)
    }

    constructor(i: Double) {
        _value = BigDecimal(i)
    }

    constructor(i: Float) {
        _value = BigDecimal(i.toDouble())
    }

    constructor(i: Short) {
        _value = BigDecimal(i.toInt())
    }

    constructor(i: Byte) {
        _value = BigDecimal(i.toInt())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitBignum(this)
    }

    override fun toString(): String {
        return _value.toString()
    }

    override fun hashCode(): Int {
        return _value.hashCode()
    }

    override fun equals(compare: Any?): Boolean {
        if (compare == null) {
            return false
        }
        if (compare.getClass() != this.getClass()) return false
        else return _value.compareTo((compare as Bignum)._value) == 0
    }

    fun divide(other: Bignum): Bignum {
        return Bignum(
            _value.divide(
                other._value, 150,
                RoundingMode.HALF_UP
            )
        )
    }

    fun lessThan(other: Bignum): Boolean {
        return if (_value.compareTo(other._value) < 0) true else false
    }

    fun greaterThan(other: Bignum): Boolean {
        return if (_value.compareTo(other._value) > 0) true else false
    }

    fun subtract(other: Bignum): Bignum {
        return (Bignum(_value.subtract(other._value)))
    }

    fun negate(): Exp {
        return (Bignum(_value.negate()))
    }

    fun multiply(other: Bignum): Exp {
        return (Bignum(_value.multiply(other._value)))
    }

    fun add(other: Bignum): Exp {
        return (Bignum(_value.add(other._value)))
    }

    fun pow(other: Bignum): Exp {
        return Bignum(
            _value.pow(
                other._value.intValueExact(),
                MathContext(100000)
            )
        )
    }

    fun remainder(other: Bignum): Exp {
        return (Bignum(_value.remainder(other._value)))
    }

    fun sqrt(): Exp {
        return Bignum(Math.sqrt(_value.doubleValue()))
    }

    fun sin(): Exp {
        return Bignum(Math.sin(_value.floatValue().toDouble()))
    }

    fun cos(): Exp {
        return Bignum(Math.cos(_value.floatValue().toDouble()))
    }

    fun atan2(other: Bignum): Exp {
        return Bignum(Math.atan2(_value.floatValue().toDouble(), other._value.floatValue().toDouble()))
    }

    fun doubleValue(): Double {
        return _value.doubleValue()
    }

    fun bigDecimalValue(): BigDecimal {
        return _value
    }

    override fun eval(env: Environment?): Exp {
        return this
    }

    fun scale(scale: Bignum): Exp {
        return (Bignum(
            _value.setScale(
                (scale._value).intValue(),
                RoundingMode.HALF_UP
            )
        ))
    }

    override fun compareTo(o: Any?): Int {
        if (o !is Bignum) {
            return -1
        }
        return this._value.compareTo(o._value)
    }
}
