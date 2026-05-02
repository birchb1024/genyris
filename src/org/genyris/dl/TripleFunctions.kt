//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class TripleFunctions(interp: Interpreter) : ApplicableFunction(interp, "triple", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun toSymbol(x: Exp): Symbol? {
        if (x is Symbol) {
            return _interp.intern(x)
        }
        if (x is Bignum || x is StrinG || x is Symbol) {
            return _interp.intern(x.toString())
        }
        throw GenyrisException("Cannot make a triple with " + x.toString() + " " + x.getClass().getName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp {
        checkArguments(arguments, 3)
        val types = arrayOf<Class<*>?>(Exp::class.java, Exp::class.java, Exp::class.java)
        checkArgumentTypes(types, arguments)
        return Triple(toSymbol(arguments[0]!!), toSymbol(arguments[1]!!), arguments[2])
    }

    abstract class AbstractTripleMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
        @kotlin.Throws(GenyrisException::class)
        protected fun getSelfAsTriple(env: Environment): Triple {
            getSelf(env)
            if (_self !is Triple) {
                throw GenyrisException(
                    "Non-Triple passed to a Triple method."
                )
            } else {
                return _self as Triple
            }
        }
    }


    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(TripleFunctions(interpreter))
        }
    }
}
