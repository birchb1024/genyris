package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.ExpressionEnvironment

abstract class Atom : ExpWithEmbeddedClasses(), Comparable<Any?> {
    @kotlin.Throws(GenyrisException::class)
    abstract override fun acceptVisitor(guest: Visitor?)

    @kotlin.Throws(GenyrisException::class)
    abstract override fun eval(env: Environment?): Exp?

    abstract override fun toString(): String

    abstract override fun getBuiltinClassSymbol(table: Internable?): Symbol?

    override fun isPair(): Boolean {
        return false
    }

    @kotlin.Throws(AccessException::class)
    override fun length(NIL: Symbol?): Int {
        throw AccessException(
            "attempt to take length of atom: "
                    + this.toString()
        )
    }

    @kotlin.Throws(AccessException::class)
    override fun nth(number: Int, NIL: Symbol?): Exp? {
        throw AccessException(
            ("attempt to take nth " + number + " of atom: "
                    + this.toString())
        )
    }

    @kotlin.Throws(AccessException::class)
    override fun car(): Exp? {
        throw AccessException(
            "attempt to take left of non-pair: "
                    + this.toString()
        )
    }

    @kotlin.Throws(AccessException::class)
    override fun cdr(): Exp? {
        throw AccessException(
            "attempt to take right of non-pair: "
                    + this.toString()
        )
    }

    @kotlin.Throws(AccessException::class)
    override fun setCar(exp: Exp?): Exp? {
        throw AccessException("attempt to set left of non-pair")
    }

    @kotlin.Throws(AccessException::class)
    override fun setCdr(exp: Exp?): Exp? {
        throw AccessException("attempt to set left of non-pair")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment? {
        return ExpressionEnvironment(parent, this)
    }

    override fun compareTo(other: Any): Int {
        if (this.getClass() != other.getClass()) {
            return -1
        } else {
            return this.toString().compareTo((other as Atom).toString())
        }
    }
}
