// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.text.CharacterCodingException

class StrinG : Atom {
    private val _value: String
    val quoteChar: Char

    constructor(str: String) {
        _value = str
        requireNotNull(str) { "null passed to String constructor" }
        this.quoteChar = '\''
    }

    constructor(str: String, quote: Char) {
        _value = str
        this.quoteChar = quote
    }

    constructor(str: StringBuffer, quotechar: Char) {
        _value = str.toString()
        this.quoteChar = quotechar
    }

    constructor(array: CharArray) {
        _value = String(array)
        this.quoteChar = '\''
    }

    constructor(c: Char) {
        val array = CharArray(1)
        array[0] = c
        _value = String(array)
        this.quoteChar = '\''
    }

    val alternateQuoteChar: Char
        get() = alternateQuoteChar(this.quoteChar)

    override fun toString(): String {
        return _value
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitStrinG(this)
    }

    @kotlin.Throws(GenyrisException::class)
    fun split(NIL: Exp?, regex: StrinG): Exp? {
        var result = NIL
        if (regex.toString() == "") {
            if (_value == "") {
                return Pair(StrinG(""), NIL)
            }
            val array: CharArray = _value.toCharArray()
            for (i in _value.length() - 1 downTo 0) {
                result = Pair(StrinG(array[i]), result)
            }
            return result
        }
        try {
            val splitted: Array<String?> = _value.split(regex._value)
            for (i in splitted.indices.reversed()) {
                result = Pair(StrinG(splitted[i]!!), result)
            }
        } catch (e: PatternSyntaxException) {
            throw GenyrisException(e.getMessage())
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    fun toInts(NIL: Exp?, charsetName: String): Exp? {
        var result = NIL
        val charset = Charset.forName(charsetName)
        val encoder = charset.newEncoder()
        try {
            val bbuf = encoder.encode(CharBuffer.wrap(_value))
            // bbuf.rewind();
            for (i in bbuf.limit() - 1 downTo 0) {
                result = Pair(
                    Bignum(0x000000FF and (bbuf.get(i).toInt())),
                    result
                )
            }
        } catch (e: CharacterCodingException) {
            throw GenyrisException(e.getMessage())
        }
        return result
    }

    fun concat(str: StrinG): StrinG {
        return StrinG(this._value.concat(str._value))
    }

    @kotlin.Throws(GenyrisException::class)
    fun ss(nil: Symbol?, true1: Symbol?, regex: StrinG): Exp? {
        try {
            return (if (_value.matches(regex._value)) true1 else nil)
        } catch (e: PatternSyntaxException) {
            throw GenyrisException(e.getMessage())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun regex(NIL: Symbol?, true1: Symbol?, regex: StrinG): Exp? {
        try {
            var retval: Exp? = NIL
            val p = Pattern.compile(regex._value)
            val m = p.matcher(_value)
            if (m.find()) {
                for (i in m.groupCount() downTo 0) {
                    val match: String? = m.group(i)
                    if (match == null) {
                        return NIL // this group did not match
                    }
                    retval = Pair.Companion.cons(StrinG(match), retval)
                }
            }
            return retval
        } catch (e: PatternSyntaxException) {
            throw GenyrisException(e.getMessage())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun match(nil: Symbol?, true1: Symbol?, regex: StrinG): Exp? {
        try {
            return (if (_value.matches(regex._value)) true1 else nil)
        } catch (e: PatternSyntaxException) {
            throw GenyrisException(e.getMessage())
        }
    }

    fun length(): Exp {
        return (Bignum(_value.length()))
    }

    @kotlin.Throws(AccessException::class)
    override fun length(NIL: Symbol?): Int {
        return _value.length()
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.STRING()
    }

    override fun hashCode(): Int {
        return _value.hashCode()
    }

    override fun equals(compare: Any?): Boolean {
        if (compare == null) return false
        if (compare.getClass() != this.getClass()) return false
        else return _value == (compare as StrinG)._value
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    fun replace(regex: StrinG, replacement: StrinG): Exp {
        return StrinG(
            _value.replace(
                regex.toString(), replacement
                    .toString()
            )
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun slice(start: BigDecimal, end: BigDecimal): Exp {
        if (this._value.length() == 0) {
            return this
        }
        val ending: Int = Math.min(this._value.length(), end.intValue())
        if (start.intValue() >= this._value.length()) {
            throw GenyrisException("String slice start " + start + " beyond end of string " + this._value.length())
        }
        if (start.intValue() > end.intValue()) {
            throw GenyrisException("String slice start " + start + " bigger than end " + end)
        }
        try {
            return StrinG(_value.substring(start.intValue(), ending))
        } catch (e: StringIndexOutOfBoundsException) {
            throw GenyrisException("String slice StringIndexOutOfBoundsException: " + e.getMessage())
        }
    }

    override fun compareTo(o: Any?): Int {
        if (o !is StrinG) {
            return -1
        }
        return this._value.compareTo(o._value)
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun makeStringFromCharset(
            NIL: Symbol?, intList: Exp,
            charsetName: String
        ): StrinG {
            var intList = intList
            val charset = Charset.forName(charsetName)
            val array = ByteBuffer.allocate(intList.length(NIL))
            var i = 0
            while (intList !== NIL) {
                val first = intList.car()
                if (first !is Bignum) {
                    throw GenyrisException(
                        "Non-Bignum passed to string constructor: "
                                + first.toString()
                    )
                }
                val integer = first
                array.put(i, integer.bigDecimalValue().intValue().toByte())
                i += 1
                intList = intList.cdr()
            }
            val cbuf = charset.decode(array)
            return StrinG(cbuf.toString())
        }

        fun alternateQuoteChar(quote: Char): Char {
            return (if (quote == '\'') '"' else '\'')
        }
    }
}
