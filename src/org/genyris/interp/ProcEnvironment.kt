// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.AccessException

class ProcEnvironment(runtime: Environment?, abstractClosure: AbstractClosure?) :
    ExpressionEnvironment(runtime, abstractClosure) {
    // This environment encompasses an applicable function or closure.
    private val SOURCE: Symbol?
    private val NAME: Symbol?

    init {
        SOURCE = _parent.getSymbolTable().SOURCE()
        NAME = _parent.getSymbolTable().NAME()
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        if (sym === SOURCE) {
            try {
                return (_theExpression as AbstractClosure).getCode()
            } catch (e: AccessException) {
                throw UnboundException(e.getMessage())
            }
        } else if (sym === NAME) {
            return StrinG((_theExpression as AbstractClosure).getName())
        } else if (sym === _vars) {
            return _theExpression.dir(this.getSymbolTable())
        } else {
            return super.lookupDynamicVariableValue(dsym)
        }
    }
}
