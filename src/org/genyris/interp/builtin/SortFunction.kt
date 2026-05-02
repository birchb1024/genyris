// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.*
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.util.*

class SortFunction(interp: Interpreter) : ApplicableFunction(interp, "sort", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment?): Exp? {
        checkArguments(arguments, 1)
        if (arguments[0] === NIL) {
            return NIL
        }
        val types = arrayOf<Class<*>?>(Pair::class.java)
        checkArgumentTypes(types, arguments)
        val theList = arguments[0] as Pair
        var head: Exp = theList
        val item = head.car()
        if (!(item is Bignum
                    || item is Pair
                    || item is PairEquals
                    || item is StrinG
                    || item is Symbol
                    || item is Triple
                    )
        ) {
            throw GenyrisException(
                "item in argument to sort must be atomic, but got " + item.getClasses(
                    envForBindOperations
                ).toString()
            )
        }
        var theClass: Class<*> = theList.car().getClass()
        // Get parent classes. . .
        if (theClass == PairEquals::class.java) {
            theClass =
                Pair::class.java // Treat PairEquals exactl the same as Pair. TODO special case should be handled by isInstance or similar.
        }
        if (theClass == PrefixSymbol::class.java || theClass == EscapedSymbol::class.java || theClass == SimpleSymbol::class.java) {
            theClass =
                Symbol::class.java // Treat all Symbols the same. TODO special case should be handled by isInstance or similar.
        }
        val tmp: MutableList<Exp?> = ArrayList<Exp?>()
        try {
            while (!head.isNil()) {
                if (!theClass.isInstance(head.car())) {
                    throw GenyrisException(
                        "item in argument to sort must be similar class " + theClass.getName() + " but was " + head.car()
                            .getClass().getName()
                    )
                }
                tmp.add(head.car())
                head = head.cdr()
            }
        } catch (e: Exception) {
            throw GenyrisException(e.getMessage())
        }
        Collections.sort<Exp?>(tmp)
        Collections.reverse(tmp)
        val it = tmp.iterator() // TODO extract method
        var result: Exp? = NIL
        while (it.hasNext()) {
            val a = it.next()
            result = Pair.Companion.cons(a, result)
        }
        return result
    }
}
