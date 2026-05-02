// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.DynamicEnvironment
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import java.util.*

open class Dictionary : Atom, Environment {
    var map: MutableMap<*, *>
        protected set

    var parent: Environment?
        protected set

    constructor() {
        this.map = mapFactory()
        this.parent = null
    }

    constructor(parent: Environment?) {
        this.map = mapFactory()
        this.parent = parent
    }

    constructor(key: Symbol?, value: Exp?, parent: Environment?) {
        this.map = mapFactory()
        map.put(key, value)
        this.parent = parent
    }

    constructor(parent: Environment?, map: HashMap<*, *>) {
        this.map = HashMap<Any?, Any?>(map)
        this.parent = parent
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitDictionary(this)
    }

    fun hasKey(a: Exp?): Boolean {
        return map.containsKey(a)
    }

    fun asAlist(): Exp {
        val NIL: Exp? = parent!!.getNil()
        val keys: SortedSet<*> = TreeSet<Any?>(map.keySet())
        val iter: MutableIterator<Exp?> = keys.iterator()
        var result = NIL
        var tail = result
        while (iter.hasNext()) {
            val key = iter.next()
            val value = map.get(key) as Exp?
            val association: Pair = PairEquals(DynamicSymbol(key as SimpleSymbol?), value)
            val newEnd = Pair(association, NIL) // TODO Add an efficient append() function for conses.
            if (tail === NIL) {
                tail = newEnd
                result = tail
            } else {
                (tail as Pair).setCdr(newEnd)
                tail = newEnd
            }
        }
        return Pair(parent!!.getSymbolTable().DICT(), result)
    }

    @kotlin.Throws(GenyrisException::class)
    fun defineVariableRaw(sym: Symbol?, valu: Exp?) {
        if (sym === CLASSES()) {
            setClasses(valu, parent!!.getNil())
        } else {
            map.put(sym, valu)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineDynamicVariable(sym: DynamicSymbol, valu: Exp?) {
        defineVariableRaw(sym.getRealSymbol(), valu)
    }

    protected fun CLASSES(): Exp? {
        return parent!!.getSymbolTable().CLASSES()
    }

    protected fun SELF(): Exp? {
        return parent!!.getSymbolTable().SELF()
    }

    private fun SUPERCLASSES(): Exp? {
        return parent!!.getSymbolTable().SUPERCLASSES()
    }

    private fun VARS(): Exp? {
        return parent!!.getSymbolTable().VARS()
    }

    override fun dir(table: Internable): Exp {
        val iter: MutableIterator<*> = map.keySet().iterator()
        var result: Exp = Pair.Companion.cons3(
            DynamicSymbol(table.SELF()),
            DynamicSymbol(table.VARS()),
            DynamicSymbol(table.CLASSES()), table.NIL()
        )
        while (iter.hasNext()) {
            val key: Exp = DynamicSymbol(iter.next() as SimpleSymbol?)
            result = Pair(key, result)
        }
        return result
    }

    @kotlin.Throws(UnboundException::class)
    private fun lookupInClasses(symbol: DynamicSymbol): Exp? {
        var classes = getClasses(this.parent)
        while (classes !== parent!!.getNil()) {
            try {
                if (classes.car() !is Environment) {
                    throw UnboundException("damaged class in lass list: " + classes.car())
                }
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
        throw UnboundException(
            "key " + symbol.toString() +
                    " not found in dict: " + this
        )
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupInThisClassAndSuperClasses(symbol: DynamicSymbol): Exp? {
        if (map.containsKey(symbol.getRealSymbol())) {
            return map.get(symbol.getRealSymbol()) as Exp?
        } else {
            return lookupInSuperClasses(symbol)
        }
    }

    @kotlin.Throws(UnboundException::class)
    private fun lookupInSuperClasses(symbol: DynamicSymbol): Exp? {
        if (!map.containsKey(SUPERCLASSES())) {
            throw UnboundException("object has no superclasses")
        }
        var superclasses = map.get(SUPERCLASSES()) as Exp
        while (superclasses !== parent!!.getNil()) {
            try {
                val klass = (superclasses.car()) as Environment
                try {
                    return klass.lookupInThisClassAndSuperClasses(symbol)
                } catch (e: UnboundException) {
                } finally {
                    superclasses = superclasses.cdr()
                }
            } catch (e: AccessException) {
                throw UnboundException("bad classes list in object")
            }
        }
        throw UnboundException(
            "key " + symbol.toString() +
                    " not found in dict: " + this
        )
    }

    @kotlin.Throws(UnboundException::class)
    override fun setVariableValue(symbol: Symbol, valu: Exp?) {
        symbol.setVariableValue(this, valu)
    }

    @kotlin.Throws(UnboundException::class)
    open fun setDynamicVariableValueRaw(sym: Symbol?, valu: Exp?) {
        if (sym === CLASSES()) {
            try {
                setClasses(valu, parent!!.getNil())
            } catch (ignore: AccessException) {
            }
        } else {
            if (map.containsKey(sym)) {
                map.put(sym, valu)
            } else {
                throw UnboundException("in object, undefined variable: " + sym)
            }
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun setDynamicVariableValue(symbol: DynamicSymbol, valu: Exp?) {
        setDynamicVariableValueRaw(symbol.getRealSymbol(), valu)
    }

    @kotlin.Throws(UnboundException::class)
    override fun setLexicalVariableValue(symbol: SimpleSymbol?, valu: Exp?) {
        throw UnboundException("Dictionary expected dynamic symbol ref " + symbol)
    }

    override fun toString(): String {
        return "<dict " + asAlist().toString() + ">"
    }

    @kotlin.Throws(UnboundException::class)
    fun lookupVariableShallow(symbol: SimpleSymbol): Exp? {
        if (symbol === CLASSES()) {
            return getClasses(this.parent)
        } else if (map.containsKey(symbol)) {
            return map.get(symbol) as Exp?
        } else {
            throw UnboundException(
                "key " + symbol.toString() +
                        " not found in dict: " + this
            )
        }
    }

    override fun getNil(): SimpleSymbol? {
        return parent!!.getNil()
    }

    override fun internString(symbolName: String?): Symbol? {
        return parent!!.internString(symbolName)
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.DICTIONARY()
    }

    override fun getSelf(): Exp {
        return this
    }

    override fun getSymbolTable(): Internable? {
        return parent!!.getSymbolTable()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupVariableValue(symbol: Symbol): Exp? {
        try {
            return symbol.lookupVariableValue(this)
        } catch (e: UnboundException) {
            throw UnboundException("variable " + symbol + " unbound in " + this)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsymbol: DynamicSymbol): Exp? {
        val symbol = dsymbol.getRealSymbol()

        if (symbol === SELF()) {
            return this
        } else if (symbol === CLASSES()) {
            return getClasses(this.parent)
        } else if (symbol === VARS()) {
            return dir(parent!!.getSymbolTable())
        } else if (map.containsKey(symbol)) {
            return map.get(symbol) as Exp?
        }
        try {
            return lookupInClasses(dsymbol)
        } catch (ignore: UnboundException) {
        }

        if (map.containsKey(SUPERCLASSES())) {
            return lookupInSuperClasses(dsymbol)
        }
        throw UnboundException("unbound " + symbol.toString())
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupLexicalVariableValue(symbol: SimpleSymbol?): Exp? {
        throw UnboundException("no lexical symbols in Dictionary: " + this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineLexicalVariable(symbol: SimpleSymbol?, valu: Exp?) {
        throw UnboundException("no lexical symbols in Dictionary: " + symbol)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineVariable(symbol: Symbol, valu: Exp?) {
        symbol.defineVariable(this, valu)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment {
        val bindings: MutableMap<*, *> = mapFactory()
        bindings.put(SELF(), this)
        return DynamicEnvironment(parent, bindings, this)
    }

    @kotlin.Throws(GenyrisException::class)
    fun addProperty(env: Environment, name: String?, value: Exp?): Dictionary {
        defineDynamicVariable(
            DynamicSymbol(env.internString(name)),
            value
        )
        return this
    }

    @kotlin.Throws(GenyrisException::class)
    fun defineInt(name: String?, i: Int) {
        defineDynamicVariable(DynamicSymbol(internString(name)), Bignum(i))
    }

    @kotlin.Throws(GenyrisException::class)
    fun defineString(name: String?, string2: String?) {
        defineDynamicVariable(DynamicSymbol(internString(name)), StrinG(string2))
    }

    @kotlin.Throws(GenyrisException::class)
    fun defineSymbol(name: String?, string2: String?) {
        defineDynamicVariable(DynamicSymbol(internString(name)), internString(string2))
    }

    override fun isBound(s: Symbol?): Boolean {
        return hasKey(s)
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || getClass() != o.getClass()) {
            return false
        }
        return this.map == (o as Dictionary).map
    }

    companion object {
        protected fun mapFactory(): MutableMap<*, *> {
            return HashMap<Any?, Any?>()
        }
    }
}
