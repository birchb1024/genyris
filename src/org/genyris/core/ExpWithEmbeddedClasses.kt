// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.classification.ClassMROComparator
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import java.util.*

abstract class ExpWithEmbeddedClasses : Exp(), Classifiable {
    private val _classes: ArrayList<*>

    init {
        _classes = ArrayList<Any?>(1)
    }

    @kotlin.Throws(GenyrisException::class)
    abstract override fun acceptVisitor(guest: Visitor?)

    private fun sortClassesinMRO(env: Environment) {
        val tmp = _classes.toArray()
        val comp: Comparator<*> = ClassMROComparator(
            env.getSymbolTable().NIL(),
            env.getSymbolTable().SUPERCLASSES()
        )
        Arrays.sort<Any?>(tmp, comp)
        _classes.clear()
        for (i in tmp.indices) _classes.add(tmp[i]) // TODO learn some Java
    }

    override fun addClass(klass: Dictionary) { // TODO change signature to
        // Dictionary
        if (_classes.contains(klass)) {
            return
        }
        _classes.add(klass)
        sortClassesinMRO(klass.getParent())
    }

    @kotlin.Throws(AccessException::class)
    override fun setClasses(classList: Exp, NIL: Exp?) {
        var classList = classList
        if (classList !is Pair) {
            throw AccessException(
                "setClasses expected a list, not "
                        + classList
            )
        }
        _classes.clear()
        while (classList !== NIL) {
            _classes.add(classList.car())
            classList = classList.cdr()
        }
    }

    override fun getClasses(env: Environment): Exp {
        val NIL: Exp? = env.getNil()
        val builtinClassSymbol = getBuiltinClassSymbol(env.getSymbolTable())
        val builtinClass: Exp?
        try {
            builtinClass = env.lookupVariableValue(builtinClassSymbol)
        } catch (e: UnboundException) {
            throw Error(
                (builtinClassSymbol
                    .toString() + " is Missing builtin class '" + builtinClassSymbol + "' - fatal!")
            )
        }
        var classes: Exp = Pair(builtinClass, NIL)
        val arryOfObjects = _classes.toArray() // TODO why convert?
        for (i in arryOfObjects.indices) {
            classes = Pair(arryOfObjects[i] as Exp?, classes)
        }
        return classes
    }

    override fun removeClass(k: Exp?) {
        val klass = k as Dictionary
        _classes.remove(klass)
        sortClassesinMRO(klass.getParent())
    }

    override fun isTaggedWith(klass: Dictionary?): Boolean {
        return _classes.contains(klass)
    }
}
