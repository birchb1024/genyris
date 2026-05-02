// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.SimpleSymbol

open class PairEnvironment  // This environment encompasses a Pair.
    (runtime: Environment?, theObject: Pair?) : ExpressionEnvironment(runtime, theObject) {
    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol, valu: Exp?) {
        val sym: Exp? = symbol.getRealSymbol()

        if (sym === _left) {
            (_theExpression as Pair).setCar(valu)
        } else if (sym === _right) {
            (_theExpression as Pair).setCdr(valu)
        } else {
            super.setDynamicVariableValue(symbol, valu)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        if (sym === _left) {
            return (_theExpression as Pair).car()
        } else if (sym === _right) {
            return (_theExpression as Pair).cdr()
        } else {
            return super.lookupDynamicVariableValue(dsym)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupLexicalVariableValue(sym: SimpleSymbol?): Exp? {
        if (sym === _left) {
            return (_theExpression as Pair).car()
        } else if (sym === _right) {
            return (_theExpression as Pair).cdr()
        } else {
            return super.lookupLexicalVariableValue(sym)
        }
    }
}
