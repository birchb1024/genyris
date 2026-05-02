//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl

import org.genyris.core.Exp
import org.genyris.core.ExpWithEmbeddedClasses
import org.genyris.core.Pair
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.interp.builtin.ObjectFunction.AbstractDictionaryMethod

object ThingMethods {
    @kotlin.Throws(UnboundException::class, GenyrisException::class)
    fun bindFunctionsAndMethods(interpreter: Interpreter) {
        interpreter.bindMethodInstance("Thing", AsGraphMethod(interpreter))
        interpreter.bindMethodInstance("Thing", AsTriplesMethod(interpreter))
    }

    class AsGraphMethod(interp: Interpreter) : ApplicableFunction(interp, "asGraph", true) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp {
            checkArguments(arguments, 1)
            val ts: AbstractGraph = GraphHashSimple()
            val self = env.getSelf() as ExpWithEmbeddedClasses
            var classes = self.getClasses(env)
            // TODO move this code int ExpWithEmbeddedClasses as a method
            while (classes !== NIL) {
                ts.add(Triple(toSymbol(arguments[0]), env.getSymbolTable().TYPE(), classes.car()))
                classes = classes.cdr()
            }
            return ts
        }
    }

    class AsTriplesMethod(interp: Interpreter?) : AbstractDictionaryMethod(interp, "asTriples") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
            var results: Exp? = NIL
            val self = env
                .getSelf() as ExpWithEmbeddedClasses
            checkArguments(arguments, 0, 1)
            val subject = if (arguments.size == 1) arguments[0] else NIL
            var classes = self.getClasses(env)
            // TODO move this code int ExpWithEmbeddedClasses as a method
            while (classes !== NIL) {
                results = Pair(
                    Triple(
                        toSymbol(subject), env.getSymbolTable()
                            .TYPE(), classes.car()
                    ), results
                )
                classes = classes.cdr()
            }
            return results
        }
    }
}
