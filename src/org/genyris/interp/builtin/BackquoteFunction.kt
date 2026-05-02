// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.*
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class BackquoteFunction(interp: Interpreter) : ApplicableFunction(interp, Constants.TEMPLATE, false) {
    private val DOLLAR: Exp?
    private val AT: Exp?

    init {
        DOLLAR = interp.getSymbolTable().DOLLAR()
        AT = interp.getSymbolTable().DOLLAR_AT()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        checkArguments(arguments, 1)
        return backQuoteAux(envForBindOperations, arguments[0]!!)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun backQuoteAux(env: Environment?, sexp: Exp): Exp? {
        if (sexp === NIL || (!sexp.isPair())) {
            return sexp
        } else {
            val list = sexp as Pair
            if (list.car() === DOLLAR) {
                return list.cdr().car().eval(env)
            }
            if (list.car().isPair() && list.car().car() === AT) {
                val res = list.car().cdr().car().eval(env)
                val rest = backQuoteAux(env, list.cdr())
                return append(res, rest)
            }
            if (list is PairSource) {
                return PairSource(
                    backQuoteAux(env, list.car()),
                    backQuoteAux(env, list.cdr()),
                    list.filename, list.lineNumber
                )
            }
            return Pair(backQuoteAux(env, list.car()), backQuoteAux(env, list.cdr()))
        }
    }

    @kotlin.Throws(AccessException::class)
    private fun append(l1: Exp, l2: Exp?): Exp? {
        if (l1 === NIL) {
            return l2
        }
        if (l1 is PairEquals) {
            return PairEquals(l1.car(), append(l1.cdr(), l2))
        }
        if (l1 is PairSource) {
            val tmp = PairSource(l1.car(), append(l1.cdr(), l2), l1.filename, l1.lineNumber)
            return tmp
        }
        return Pair(l1.car(), append(l1.cdr(), l2))
    }
}
