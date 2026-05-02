package org.genyris.core

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment

class Biscuit(val expression: Exp) : Atom(), Comparable<Any?> {
    override fun toString(): String {
        return "<" + this.getClass().getName() + " " + expression.toString() + ">"
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitBiscuit(this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.BISCUIT()
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }
}
