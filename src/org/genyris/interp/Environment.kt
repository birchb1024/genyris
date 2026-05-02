// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.GenyrisException

interface Environment {
    @kotlin.Throws(UnboundException::class)
    fun lookupVariableValue(symbol: Symbol?): Exp?

    @kotlin.Throws(UnboundException::class)
    fun lookupDynamicVariableValue(symbol: DynamicSymbol?): Exp?

    @kotlin.Throws(UnboundException::class)
    fun lookupLexicalVariableValue(symbol: SimpleSymbol?): Exp?

    @kotlin.Throws(GenyrisException::class)
    fun defineVariable(symbol: Symbol?, valu: Exp?)

    @kotlin.Throws(GenyrisException::class)
    fun defineLexicalVariable(symbol: SimpleSymbol?, valu: Exp?)

    @kotlin.Throws(GenyrisException::class)
    fun defineDynamicVariable(symbol: DynamicSymbol?, valu: Exp?)

    @kotlin.Throws(UnboundException::class)
    fun setVariableValue(symbol: Symbol?, valu: Exp?)

    @kotlin.Throws(UnboundException::class)
    fun setLexicalVariableValue(symbol: SimpleSymbol?, valu: Exp?)

    @kotlin.Throws(UnboundException::class)
    fun setDynamicVariableValue(symbol: DynamicSymbol?, valu: Exp?)


    override fun toString(): String

    @kotlin.Throws(UnboundException::class)
    fun lookupInThisClassAndSuperClasses(symbol: DynamicSymbol?): Exp?

    val nil: SimpleSymbol?

    fun internString(symbolName: String?): Symbol?

    @get:kotlin.Throws(UnboundException::class)
    val self: Exp?

    val symbolTable: Internable?
    fun isBound(s: Symbol?): Boolean
}