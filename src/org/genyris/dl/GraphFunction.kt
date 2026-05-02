//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class GraphFunction(interp: Interpreter) : ApplicableFunction(interp, "graph", true) {
    private val _tripleSymbol: Symbol?

    init {
        _tripleSymbol = _interp.intern("triple")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp {
        val ts: AbstractGraph = GraphHashSimple()
        for (i in arguments.indices) {
            addTripleFromList(ts, arguments[i]!!)
        }
        return ts
    }

    @kotlin.Throws(GenyrisException::class)
    private fun addTripleFromList(ts: AbstractGraph, exp: Exp) {
        // Accept either (s p o) or ^(triple s p o) or a Triple
        // also accept (s = o) -> (triple nil s 0)
        when (exp) {
            -> {
                if (ep.length(NIL) == 1 && ep.cdr() !== NIL) { // (s = o)
                    if (ep.car() !is Symbol) {
                        throw GenyrisException("triple predicate must be a symbol in: " + exp.toString())
                    }
                    ts.add(Triple(NIL, ep.car() as Symbol?, ep.cdr()))
                    return
                }
                if (ep.length(NIL) == 4 && ep.car() == _tripleSymbol) {
                    ep = ep.cdr() as Pair?
                }
                val subject = toSymbol(ep.car())
                val predicate = toSymbol(ep.cdr().car())
                val `object`: Exp? = ep.cdr().cdr().car()
                ts.add(Triple(subject, predicate, `object`))
            }

            -> ts.add(t)
            else -> throw GenyrisException("unknown type in graph argument:" + exp.toString())
        }
    }

    abstract class AbstractGraphMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
        @kotlin.Throws(GenyrisException::class)
        protected fun getSelfGraph(env: Environment): AbstractGraph {
            getSelf(env)
            if (_self !is AbstractGraph) {
                throw GenyrisException(
                    "Non-Graph passed to a Graph method."
                )
            } else {
                return _self as AbstractGraph
            }
        }
    }

    class AddMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "add") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            val self = getSelfGraph(env)
            checkArguments(arguments, 1, 3)
            if (arguments.size == 1) {
                val types = arrayOf<Class<*>?>(Triple::class.java)
                checkArgumentTypes(types, arguments)
                self.add(arguments[0] as Triple?)
                return _self
            }

            checkArguments(arguments, 2, 3)
            val types = arrayOf<Array<Class<*>?>?>(
                arrayOf<Class<*>?>(Symbol::class.java),
                arrayOf<Class<*>?>(Symbol::class.java, Pair::class.java),
                arrayOf<Class<*>?>(
                    Exp::class.java
                )
            )
            checkSuppliedArgumentTypes(types, arguments)
            if (arguments[1] is Pair) {
                var head = arguments[1]
                while (head !== NIL) {
                    if (head.car() !is Pair) {
                        throw GenyrisException("non-pair in .add predicate " + head.car())
                    }
                    self.add(Triple(toSymbol(arguments[0]), toSymbol(head.car().car()), head.car().cdr()))
                    head = head.cdr()
                }
                return _self
            }
            self.add(Triple(toSymbol(arguments[0]), toSymbol(arguments[1]), arguments[2]))
            return _self
        }
    }

    class GetMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "get") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            val self = getSelfGraph(env)
            checkArguments(arguments, 2)
            val types = arrayOf<Class<*>?>(Exp::class.java, Exp::class.java)
            checkArgumentTypes(types, arguments)
            return self.get(toSymbol(arguments[0]), toSymbol(arguments[1]))
        }
    }

    class GetListMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "get-list") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            val self = getSelfGraph(env)
            checkArguments(arguments, 2)
            val types = arrayOf<Class<*>?>(Exp::class.java, Exp::class.java)
            checkArgumentTypes(types, arguments)
            return self.getList(toSymbol(arguments[0]), toSymbol(arguments[1]), NIL)
        }
    }

    class PutMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "put") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            val self = getSelfGraph(env)
            checkArguments(arguments, 3)
            val types = arrayOf<Class<*>?>(Symbol::class.java, Symbol::class.java, Exp::class.java)
            checkArgumentTypes(types, arguments)
            self.put(toSymbol(arguments[0]), toSymbol(arguments[1]), arguments[2])
            return _self
        }
    }

    class SubjectsMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "subjects") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            val self = getSelfGraph(env)
            return self.subjects(NIL)
        }
    }

    class PredicatesMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "predicates") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            checkArguments(arguments, 1)
            val types = arrayOf<Class<*>?>(Symbol::class.java)
            checkArgumentTypes(types, arguments)
            val self = getSelfGraph(env)
            val subject = arguments[0] as Symbol?
            return self.predicates(subject, NIL)
        }
    }

    class UnionMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "union") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            checkArguments(arguments, 1)
            val types = arrayOf<Class<*>?>(AbstractGraph::class.java)
            checkArgumentTypes(types, arguments)
            val self = getSelfGraph(env)
            val other = arguments[0] as AbstractGraph?
            return self.union(other)
        }
    }

    class LengthMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "length") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp {
            val self = getSelfGraph(env)
            return Bignum(self.length())
        }
    }

    class SelectMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "select") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
            val closure: Closure? = null
            val self = getSelfGraph(env)
            checkMinArguments(arguments, 3)
            val types = arrayOf<Class<*>?>(Exp::class.java, Exp::class.java, Exp::class.java)
            checkArgumentTypes(types, arguments)
            var subject = toSymbol(arguments[0])
            var predicate = toSymbol(arguments[1])
            var `object`: Exp? = arguments[2]
            if (arguments[0] === NIL) {
                subject = null
            }
            if (arguments[1] === NIL) {
                predicate = null
            }
            if (arguments[2] === NIL) {
                `object` = null
            }
            /* #TODO			if (arguments.length == 4 && arguments[3] != NIL) {
				closure = (Closure) arguments[3];
			}
*/
            return self.select(subject, predicate, `object`, closure, env)
        }
    }

    class AsTriplesMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "asTriples") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            val self = getSelfGraph(env)
            return self.asTripleList(NIL)
        }
    }

    class RemoveMethod(interp: Interpreter?) : AbstractGraphMethod(interp, "remove") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp {
            val self = getSelfGraph(env)
            checkArguments(arguments, 1)
            val types = arrayOf<Class<*>?>(Triple::class.java)
            checkArgumentTypes(types, arguments)
            self.remove(arguments[0] as Triple?)
            return self
        }
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(GraphFunction(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, AddMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, AsTriplesMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, GetListMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, GetMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, LengthMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, PredicatesMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, PutMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, RemoveMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, SelectMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, SubjectsMethod(interpreter))
            interpreter.bindMethodInstance(Constants.GRAPH, UnionMethod(interpreter))
        }
    }
}
