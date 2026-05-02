// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class SystemGetenvMethod(interp: Interpreter?) : AbstractMethod(interp, "getenv") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        if (arguments.size == 1) {
            val key: String? = arguments[0].toString()
            val value = System.getenv(key)
            if (value != null) {
                return StrinG(value)
            } else {
                return NIL
            }
        } else {
            val environ: MutableMap<*, *> = System.getenv()
            val iter: MutableIterator<*> = environ.keySet().iterator()
            var result: Exp? = NIL
            while (iter.hasNext()) {
                val key = iter.next() as String?
                val value = environ.get(key as Any?) as String?
                result = Pair(
                    PairEquals(StrinG(key), StrinG(value)),
                    result
                )
            }
            return result
        }
    }


    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.OS, SystemGetenvMethod(interpreter))
        }
    }
}
