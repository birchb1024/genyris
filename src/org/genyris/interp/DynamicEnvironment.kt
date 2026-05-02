// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException

class DynamicEnvironment(
    runtime: Environment?,
    bindings: MutableMap<*, *>?, // This environment merges two into one - a dynamic and a lexical.
    private val _object: Environment
) : StandardEnvironment(runtime, bindings) {
    @kotlin.Throws(GenyrisException::class)
    override fun defineDynamicVariable(symbol: DynamicSymbol?, valu: Exp?) {
        _object.defineDynamicVariable(symbol, valu)
    }

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol?, valu: Exp?) {
        _object.setDynamicVariableValue(symbol, valu)
    }

    override fun toString(): String {
        return "<DynamicEnvironment on: " + _object.toString() + ">"
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(symbol: DynamicSymbol?): Exp? {
        return _object.lookupDynamicVariableValue(symbol)
    }

    @kotlin.Throws(UnboundException::class)
    override fun getSelf(): Exp? {
        return _object.getSelf()
    }
}
