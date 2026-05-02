package org.genyris.dl

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment

class Triple(val subject: Symbol, val predicate: Symbol, val `object`: Exp) : Atom(), Comparable<Any?> {
    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitTriple(this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment {
        return TripleEnvironment(parent, this)
    }

    override fun toString(): String {
        return "(triple " + subject + " " + predicate + " " + `object` + ")"
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.TRIPLE()
    }

    override fun hashCode(): Int {
        return subject.hashCode() + predicate.hashCode() + `object`.hashCode()
    }

    override fun equals(compare: Any?): Boolean {
        if (compare !is Triple) {
            return false
        }
        val t = compare
        return subject == t.subject && predicate == t.predicate && `object` == t.`object`
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    override fun compareTo(O: Any?): Int {
        if (O !is Triple) {
            return -1
        }
        val other = O
        var comparison = subject.compareTo(other.subject)
        if (comparison != 0) {
            return comparison
        }
        comparison = predicate.compareTo(other.predicate)
        if (comparison != 0) {
            return comparison
        }
        return `object`.compareTo(other.`object`)
    }

    override fun dir(table: Internable): Exp {
        return Pair.Companion.cons3(
            table.SUBJECT(),
            table.PREDICATE(),
            table.OBJECT(),
            Pair.Companion.cons2(table.VARS(), table.CLASSES(), table.NIL())
        )
    }
}
