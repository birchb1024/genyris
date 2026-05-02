// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import kotlin.collections.HashMap
import kotlin.collections.MutableMap

// TODO Break this into a Root environment and a Standard Env....
open class StandardEnvironment : AbstractEnvironment {
    var _frame: MutableMap<*, *> // Exp, Exp

    protected var _parent: Environment?

    protected var NIL: SimpleSymbol? = null

    protected var _self: Symbol? = null
    protected var _classes: Symbol? = null
    protected var _superclasses: Symbol? = null
    protected var _classname: Symbol? = null
    protected var _vars: Symbol? = null

    protected var _left: Symbol? = null
    protected var _right: Symbol? = null
    protected var _dynamic: Symbol? = null
    protected var _lineNumber: Symbol? = null
    protected var _filename: Symbol? = null

    private var _table: Internable? = null

    constructor(table: Internable?, nil: NilSymbol?) {
        _parent = null
        _frame = mapFactory()
        NIL = nil
        _table = table

        initConstants(table)
    }

    private fun initConstants(table: Internable?) {
        if (table == null) return
        _self = table.SELF()
        _vars = table.VARS()
        _classes = table.CLASSES()
        _superclasses = table.SUPERCLASSES()
        _classname = table.CLASSNAME()
        _left = table.LEFT()
        _right = table.RIGHT()
        _dynamic = table.DYNAMIC_SYMBOL()
        _lineNumber = table.LINENUMBER()
        _filename = table.FILENAME()
    }

    private fun init() {
        NIL = _parent!!.getNil()
        val table = _parent!!.getSymbolTable()
        initConstants(table)
    }

    constructor(parent: Environment?) {
        _parent = parent
        _frame = mapFactory()
        init()
    }

    constructor(parent: Environment?, bindings: MutableMap<*, *>) {
        _parent = parent
        _frame = bindings
        init()
    }

    @kotlin.Throws(UnboundException::class)
    override fun setLexicalVariableValue(symbol: SimpleSymbol, valu: Exp?) {
        if (_frame.containsKey(symbol)) {
            _frame.put(symbol, valu)
        } else if (_parent == null) {
            throw UnboundException("unbound: " + symbol.toString())
        } else {
            _parent!!.setLexicalVariableValue(symbol, valu)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineLexicalVariable(symbol: SimpleSymbol?, valu: Exp?) {
        _frame.put(symbol, valu)
    }

    override fun toString(): String {
        return (if (_parent != null) _parent.toString() else "/") + _frame.toString()
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupInThisClassAndSuperClasses(symbol: DynamicSymbol?): Exp? {
        throw UnboundException(
            "lookupInSuperClasses not implemented for standard environments."
        )
    }

    override fun getNil(): SimpleSymbol? {
        return NIL
    }

    override fun internString(symbolName: String?): Symbol? {
        if (_table == null) {
            return _parent!!.internString(symbolName)
        } else {
            return _table!!.internString(symbolName)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun getSelf(): Exp? {
        throw UnboundException(
            "no dynamic variable self in standard environments."
        )
    }

    override fun getSymbolTable(): Internable? {
        if (_table != null) {
            return _table
        } else {
            return _parent!!.getSymbolTable()
        }
    }


    @kotlin.Throws(UnboundException::class)
    override fun lookupLexicalVariableValue(symbol: SimpleSymbol): Exp? {
        val result = _frame.get(symbol)
        if (result != null) {
            return result as Exp
        } else if (_parent == null) {
            throw UnboundException("unbound variable: " + symbol.toString())
        } else {
            return _parent!!.lookupVariableValue(symbol)
        }
    }

    override fun isBound(symbol: Symbol?): Boolean {
        // TOD DRY
        val result = _frame.get(symbol)
        if (result != null) {
            return true
        } else if (_parent == null) {
            return false
        } else {
            return _parent!!.isBound(symbol)
        }
    }

    companion object {
        protected fun mapFactory(): MutableMap<*, *> {
            return HashMap<Any?, Any?>()
        }
    }
}
