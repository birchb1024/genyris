// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import java.lang.String
import kotlin.Any
import kotlin.Boolean
import kotlin.Int

open class SimpleSymbol : Symbol {
    protected var _printName: String

    constructor(newSym: Int) {
        _printName = String.valueOf(newSym)
    }

    constructor(newSym: kotlin.String) {
        _printName = newSym
    }

    override fun getPrintName(): String {
        return _printName
    }

    override fun hashCode(): Int {
        return _printName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SimpleSymbol) {
            return false
        }
        return _printName == other._printName
    }

    override fun compareTo(arg0: Any?): Int {
        if (arg0 !is SimpleSymbol) {
            return -1
        }
        return this.getPrintName().compareTo(arg0.getPrintName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitSimpleSymbol(this)
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.SIMPLESYMBOL()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineVariable(env: Environment, valu: Exp?) {
        env.defineLexicalVariable(this, valu)
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupVariableValue(env: Environment): Exp? {
        return env.lookupLexicalVariableValue(this)
    }

    @kotlin.Throws(UnboundException::class)
    override fun setVariableValue(env: Environment, valu: Exp?) {
        env.setLexicalVariableValue(this, valu)
    }
}
