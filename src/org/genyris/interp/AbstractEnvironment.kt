// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.GenyrisException

abstract class AbstractEnvironment : Environment {
    @kotlin.Throws(UnboundException::class)
    override fun lookupVariableValue(symbol: Symbol): Exp? {
        return symbol.lookupVariableValue(this)
    }

    @kotlin.Throws(UnboundException::class)
    abstract override fun getSelf(): Exp?

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(symbol: DynamicSymbol?): Exp? {
        throw UnboundException(
            "no dynamic variable in environment: "
                    + symbol
        )
    }

    @kotlin.Throws(UnboundException::class)
    abstract override fun lookupLexicalVariableValue(symbol: SimpleSymbol?): Exp?

    @kotlin.Throws(GenyrisException::class)
    override fun defineVariable(symbol: Symbol, valu: Exp?) {
        if (symbol.isNil()) {
            throw GenyrisException("Attempt to re-define nil!")
        }
        symbol.defineVariable(this, valu)
    }

    @kotlin.Throws(GenyrisException::class)
    abstract override fun defineLexicalVariable(symbol: SimpleSymbol?, valu: Exp?)

    @kotlin.Throws(GenyrisException::class)
    override fun defineDynamicVariable(symbol: DynamicSymbol?, valu: Exp?) {
        throw GenyrisException(
            "defineDynamicVariable: no dynamic variable in environment: "
                    + symbol
        )
    }

    abstract override fun getNil(): SimpleSymbol?

    abstract override fun getSymbolTable(): Internable?

    abstract override fun internString(symbolName: String?): Symbol?

    @kotlin.Throws(UnboundException::class)
    abstract override fun lookupInThisClassAndSuperClasses(symbol: DynamicSymbol?): Exp?

    @kotlin.Throws(UnboundException::class)
    override fun setVariableValue(symbol: Symbol, valu: Exp?) {
        if (symbol.isNil()) {
            throw UnboundException("Attempt to set to nil!")
        }
        symbol.setVariableValue(this, valu)
    }

    @kotlin.Throws(UnboundException::class)
    abstract override fun setLexicalVariableValue(symbol: SimpleSymbol?, valu: Exp?)

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol?, valu: Exp?) {
        throw UnboundException(
            "setDynamicVariableValue: no lexical variable in environment: "
                    + symbol
        )
    }
}