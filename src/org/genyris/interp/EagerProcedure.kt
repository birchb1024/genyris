// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.core.Symbol
import org.genyris.core.Visitor
import org.genyris.exception.GenyrisException

class EagerProcedure  // I DO evaluate my arguments before being applied.
    (env: Environment?, expression: Exp?, appl: ApplicableFunction?) : AbstractClosure(env, expression, appl) {
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.EAGERPROC()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun computeArguments(env: Environment, exp: Exp): Array<Exp?> {
        var exp = exp
        val NIL: Symbol? = env.getNil()
        var i = 0
        val result = arrayOfNulls<Exp>(exp.length(NIL))
        while (exp.isPair()) {
            result[i] = exp.car().eval(env)
            exp = exp.cdr()
            i++
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitEagerProc(this)
    }

    override fun toString(): String {
        return "<EagerProc: " + _functionToApply.toString() + ">"
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }
}
