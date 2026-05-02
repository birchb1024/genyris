// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.java

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.ExpressionEnvironment
import org.genyris.interp.UnboundException

class JavaWrapperEnvironment(runtime: Environment, theObject: JavaWrapper?) :
    ExpressionEnvironment(runtime, theObject) {
    private val _javaclass: Symbol?

    init {
        _javaclass = runtime.getSymbolTable().JAVACLASS()
    }

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol, valu: Exp?) {
        val sym: Exp = symbol.getRealSymbol()
        val it = _theExpression as JavaWrapper
        try {
            it.setField(sym.toString(), valu, this)
        } catch (e: GenyrisException) {
            throw UnboundException(e.getMessage())
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        val it = _theExpression as JavaWrapper
        if (sym === _vars) {
            return it.dir(getSymbolTable())
        } else if (sym === _javaclass) {
            return StrinG(it.getValue().getClass().getName())
        } else if (it.hasField(sym)) {
            return it.getField(this, sym)
        } else {
            return super.lookupDynamicVariableValue(dsym)
        }
    }
}
