// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment

//
// Exp is short for 'Expression'
//
abstract class Exp : Classifiable, Closure, Comparable<Any?> {
    @kotlin.Throws(GenyrisException::class)
    abstract fun acceptVisitor(guest: Visitor?)

    @kotlin.Throws(GenyrisException::class)
    override fun computeArguments(ignored: Environment?, exp: Exp?): Array<Exp?> {
        val args = arrayOf<Exp?>(exp)
        return args
    }

    @kotlin.Throws(GenyrisException::class)
    fun evalCatchOverFlow(env: Environment?): Exp? {
        try {
            return this.eval(env)
        } catch (e: StackOverflowError) {
            throw GenyrisException("Stack Overflow")
        }
    }

    @kotlin.Throws(GenyrisException::class)
    abstract fun eval(env: Environment?): Exp?

    @kotlin.Throws(GenyrisException::class)
    open fun evalSequence(env: Environment?): Exp? {
        throw GenyrisException("Call to abstract evalSequence.")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun applyFunction(environment: Environment?, arguments: Array<Exp?>): Exp? {
        if (arguments[0]!!.isNil) {
            return this
        }
        val newEnv = this.makeEnvironment(environment)
        if (arguments[0]!!.isPair) {
            return arguments[0]!!.evalSequence(newEnv)
        } else {
            throw GenyrisException(
                ("Arguments to " + this
                        + " must be a list.")
            )
        }
    }

    @kotlin.Throws(GenyrisException::class)
    abstract fun makeEnvironment(parent: Environment?): Environment?

    open val isNil: Boolean
        get() = false

    @kotlin.Throws(AccessException::class)
    abstract fun car(): Exp?

    @kotlin.Throws(AccessException::class)
    abstract fun cdr(): Exp?

    @kotlin.Throws(AccessException::class)
    abstract fun setCar(exp: Exp?): Exp?

    @kotlin.Throws(AccessException::class)
    abstract fun setCdr(exp: Exp?): Exp?

    abstract val isPair: Boolean

    @kotlin.Throws(AccessException::class)
    abstract fun length(NIL: Symbol?): Int

    @kotlin.Throws(AccessException::class)
    abstract fun nth(number: Int, NIL: Symbol?): Exp?

    abstract override fun toString(): String

    open fun dir(table: Internable): Exp? {
        return Pair.Companion.cons3(
            DynamicSymbol(table.SELF()), DynamicSymbol(
                table.VARS()
            ), DynamicSymbol(table.CLASSES()), table.NIL()
        )
    }

    override fun getBody(nil: Exp?): Exp? {
        return nil
    }

    override fun getPrintableFrame(NIL: Exp?): Exp {
        val body = getBody(NIL)
        var location = NIL
        if (body is PairSource) {
            val source = body
            location = Pair.Companion.cons2(
                Bignum(source.lineNumber), StrinG(
                    source.filename
                ), NIL
            )
        }
        val frame: Exp = Pair.Companion.cons(StrinG(toString()), location)
        return frame
    }


    abstract override fun compareTo(o: Any?): Int

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun assertIsSymbol(predicate: Exp?, message: String?) {
            if (predicate !is SimpleSymbol) {
                throw GenyrisException(message + predicate)
            }
        }
    }
}
