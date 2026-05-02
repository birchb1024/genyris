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

class LazyProcedure(env: Environment?, expression: Exp?, appl: ApplicableFunction?) :
    AbstractClosure(env, expression, appl) {
    // I DO NOT evaluate my arguments before being applied.
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.LAZYPROC()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun computeArguments(env: Environment, exp: Exp): Array<Exp?> {
        return makeExpArrayFromList(exp, env.getNil())
    }

    @kotlin.Throws(GenyrisException::class)
    private fun makeExpArrayFromList(exp: Exp, NIL: Symbol?): Array<Exp?> {
        var exp = exp
        var i = 0
        val result = arrayOfNulls<Exp>(exp.length(NIL))
        while (exp.isPair()) {
            result[i] = exp.car()
            exp = exp.cdr()
            i++
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitLazyProc(this)
    }

    override fun toString(): String {
        return "<LazyProcedure: " + _functionToApply.toString() + ">"
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    @kotlin.Throws(GenyrisException::class)
    override fun checkTooManyArgumentCount(arguments: Array<Exp?>?) {
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }
}