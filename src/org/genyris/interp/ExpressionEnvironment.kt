// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException

open class ExpressionEnvironment(
    runtime: Environment?, // This environment encompasses an expression (Exp) which provides
    // the first place to look for slots.
    protected var _theExpression: Exp
) : StandardEnvironment(runtime, StandardEnvironment.Companion.mapFactory()) {
    @kotlin.Throws(UnboundException::class)
    override fun lookupInThisClassAndSuperClasses(symbol: DynamicSymbol): Exp? {
        throw UnboundException("unbound symbol " + symbol.toString() + " in environment " + _theExpression)
    }

    @kotlin.Throws(UnboundException::class)
    protected fun lookupInClasses(symbol: DynamicSymbol): Exp? {
        var classes = _theExpression.getClasses(_parent)
        while (classes !== NIL) {
            try {
                val klass = (classes.car()) as Environment
                try {
                    return klass.lookupInThisClassAndSuperClasses(symbol)
                } catch (e: UnboundException) {
                } finally {
                    classes = classes.cdr()
                }
            } catch (e: AccessException) {
                throw UnboundException("bad classes list in object")
            }
        }
        throw UnboundException(" unbound symbol: " + symbol.toString() + " in environment " + _theExpression)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineDynamicVariable(sym: DynamicSymbol, valu: Exp?) {
        if (sym.getRealSymbol() === _classes) {
            _theExpression.setClasses(valu, NIL)
        } else if (sym.getRealSymbol() === _self) {
            throw GenyrisException("cannot re-define self.")
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol, valu: Exp?) {
        val sym: Exp? = symbol.getRealSymbol()

        if (sym === _classes) {
            try {
                _theExpression.setClasses(valu, NIL)
            } catch (e: AccessException) {
                throw UnboundException(e.getMessage())
            }
            return
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        if (sym === _classes) {
            return _theExpression.getClasses(_parent)
        } else if (sym === _self) {
            return _theExpression
        } else if (sym === _vars) {
            return _theExpression.dir(this.getSymbolTable())
        } else {
            return lookupInClasses(dsym)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun getSelf(): Exp {
        return _theExpression
    }
}
