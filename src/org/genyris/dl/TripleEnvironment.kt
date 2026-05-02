// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.SimpleSymbol
import org.genyris.core.Symbol
import org.genyris.interp.Environment
import org.genyris.interp.ExpressionEnvironment
import org.genyris.interp.UnboundException

class TripleEnvironment(runtime: Environment, theObject: Triple?) : ExpressionEnvironment(runtime, theObject) {
    // This environment encompasses a Triple
    // All TODO
    var _subject: SimpleSymbol?
    var _predicate: SimpleSymbol?
    var _object: SimpleSymbol?

    init {
        _subject = runtime.getSymbolTable().SUBJECT()
        _predicate = runtime.getSymbolTable().PREDICATE()
        _object = runtime.getSymbolTable().OBJECT()
    }

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol?, valu: Exp?) {
        throw UnboundException(
            "Attempt to alter immutable triple: "
                    + _theExpression.toString()
        )
    }

    @kotlin.Throws(UnboundException::class)
    fun lookupLexicalVariableValue(sym: Symbol?): Exp? {
        if (sym === _subject) { // TODO equals not ==
            return (_theExpression as Triple).subject
        } else if (sym === _predicate) {
            return (_theExpression as Triple).predicate
        } else if (sym === _object) {
            return (_theExpression as Triple).`object`
        } else {
            return super.lookupLexicalVariableValue(sym as SimpleSymbol?)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        if (sym === _subject) { // TODO equals not ==
            return (_theExpression as Triple).subject
        } else if (sym === _predicate) {
            return (_theExpression as Triple).predicate
        } else if (sym === _object) {
            return (_theExpression as Triple).`object`
        } else {
            return super.lookupDynamicVariableValue(dsym)
        }
    }
}
