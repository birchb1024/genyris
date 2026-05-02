// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.*
import org.genyris.dl.AbstractGraph
import org.genyris.dl.GraphHashSimple
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class ObjectFunction(interp: Interpreter) : ApplicableFunction(interp, "dict", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(ignored: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        val dict = Dictionary(env)
        for (i in arguments.indices) {
            if (!arguments[i]!!.isPair()) throw GenyrisException("argument to dict not a list")
            if (arguments[i]!!.car() !is DynamicSymbol) throw GenyrisException("argument to dict not a dynamic symbol")
            dict.defineVariable(
                arguments[i]!!.car() as Symbol?,
                arguments[i]!!.cdr().eval(env)
            )
        }
        return dict
    }

    abstract class AbstractDictionaryMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
        @kotlin.Throws(GenyrisException::class)
        protected fun getSelfAsDictionary(env: Environment): Dictionary {
            getSelf(env)
            if (_self !is Dictionary) {
                throw GenyrisException(
                    "Non-Dictionary passed to a Dictionary method."
                )
            } else {
                return _self as Dictionary
            }
        }
    }

    class AsTriplesMethod(interp: Interpreter?) : AbstractDictionaryMethod(interp, "asTriples") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
            val self = getSelfAsDictionary(env)
            checkArguments(arguments, 1)
            var alist = self.asAlist().cdr()

            // TODO move this code int Dictionary as a method
            // TODO also collect ExpWithEmbeddedClasses classes triples.
            var results: Exp? = NIL
            while (alist !== NIL) {
                results = Pair(
                    Triple(
                        toSymbol(arguments[0]),
                        toSymbol(((alist.car().car()) as DynamicSymbol).getRealSymbol()),
                        alist.car().cdr()
                    ),
                    results
                )
                alist = alist.cdr()
            }
            return results
        }
    }

    class AsGraphMethod(interp: Interpreter?) : AbstractDictionaryMethod(interp, "asGraph") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp {
            val self = getSelfAsDictionary(env)
            checkArguments(arguments, 1)
            // #TODO move this code int Dictionary?
            // #TODO also collect ExpWithEmbeddedClasses classes triples?
            var alist = self.asAlist().cdr()
            val results: AbstractGraph = GraphHashSimple()
            while (alist !== NIL) {
                results.add(
                    Triple(
                        toSymbol(arguments[0]),
                        toSymbol(((alist.car().car()) as DynamicSymbol).getRealSymbol()),
                        alist.car().cdr()
                    )
                )
                alist = alist.cdr()
            }
            return results
        }
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(
                Constants.DICTIONARY,
                AsTriplesMethod(interpreter)
            )
            interpreter.bindMethodInstance(
                Constants.DICTIONARY,
                AsGraphMethod(interpreter)
            )
        }
    }
}
