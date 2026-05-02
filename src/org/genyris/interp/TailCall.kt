package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.GenyrisException

class TailCall(var proc: Closure, var arguments: Array<Exp?>) : Atom() {
    override fun toString(): String {
        var repr = "TailCall " + proc.toString() + " "
        for (i in arguments.indices) {
            repr += arguments[i].toString() + " "
        }
        return repr
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitTailCall(this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.TAILCALL()
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }
}
