// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.builtin.TagFunction
import kotlin.collections.HashMap
import kotlin.collections.MutableMap

open class ClassicFunction(name: String?, interp: Interpreter) : ApplicableFunction(interp, name, true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        closure: Closure?, arguments: Array<Exp?>,
        runtimeEnviron: Environment?
    ): Exp? {
        var result = bindAndExecuteSimple(closure, arguments, runtimeEnviron)

        while (result is TailCall) {
            val tc = result
            result = bindAndExecuteSimple(tc.proc, tc.arguments, runtimeEnviron)
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    fun bindAndExecuteSimple(
        closure: Closure?, arguments: Array<Exp?>,
        runtimeEnviron: Environment?
    ): Exp? {
        // TODO clean it up. What a right royal mess
        // Yeah you can say that again.

        if (closure !is AbstractClosure) {
            throw GenyrisException(
                "type missmatch - was expecting an AbstractClosure"
            )
        }
        val proc = closure
        proc.checkTooFewArgumentCount(arguments)
        proc.checkTooManyArgumentCount(arguments)
        val bindings: MutableMap<*, *> = HashMap<Any?, Any?>(proc.getNumberOfRequiredArguments())
        var i = 0
        var formals = proc._lambdaExpression.cdr().car()
        while (formals !== NIL) {
            if (formals !is Pair) {
                break // skip the return class
            }
            var formal = formals.car()
            if (formal === REST) {
                if (!formals.cdr().isPair()) {
                    throw GenyrisException("Syntax error &rest arguments has no formal parameter: " + arguments)
                }
                val actuals = assembleListFromRemainingArgs(arguments, i)
                formal = formals.cdr().car()
                if (formal !== NIL) {
                    bindings.put(formal, actuals)
                }
                i++
                break
            } else if (formal !== NIL) {
                if (formal is Pair) {
                    val left = formal.car()
                    val right = formal.cdr()
                    if (left !is Symbol) {
                        throw GenyrisException(
                            "function argument not a symbol: "
                                    + left.toString()
                        )
                    }
                    if (right !is Symbol) {
                        throw GenyrisException(
                            "function argument class spec not a symbol: "
                                    + right.toString()
                        )
                    }
                    val klass = proc.getEnv().lookupVariableValue(
                        right
                    )
                    StandardClass.Companion.assertIsThisObjectAClass(klass)
                    try {
                        TagFunction.Companion.validateObjectInClass(
                            proc.getEnv(),
                            arguments[i], klass as StandardClass
                        )
                    } catch (e: GenyrisException) {
                        throw GenyrisException(
                            ("Type mismatch in function call for (" + left
                                    + " " + Constants.CDRCHAR + " " + right
                                    + ") because " + e.getMessage())
                        )
                    }
                    bindings.put(left, arguments[i])
                } else if (formal !is Symbol) {
                    throw GenyrisException(
                        "function argument not a symbol: "
                                + formal.toString()
                    )
                } else {
                    bindings.put(formal, arguments[i])
                }
            }
            formals = formals.cdr()
            i++
        }

        // Use the procedure's frame to get lexical scope
        // and the dynamic environment for the object stuff.
        val newEnv: Environment = DynamicEnvironment(
            proc.getEnv(), bindings,
            runtimeEnviron
        )

        val result = proc.getBody(NIL).evalSequence(newEnv)
        val returnClass = proc.getReturnClassOrNull()
        if (returnClass != null) {
            try {
                TagFunction.Companion.validateObjectInClass(
                    proc.getEnv(), result,
                    returnClass
                )
            } catch (e: GenyrisTypeMismatchException) {
                throw GenyrisTypeMismatchException(
                    "return type "
                            + e.getMessage()
                )
            }
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class, AccessException::class)
    private fun assembleListFromRemainingArgs(arguments: Array<Exp?>, i: Int): Exp? {
        if (arguments.size <= i) {
            return NIL
        }
        val actuals = Pair(arguments[i], NIL)
        var tail = actuals
        for (j in i + 1..<arguments.size) {
            val newTail = Pair(arguments[j], NIL)
            tail.setCdr(newTail)
            tail = newTail
        }
        return actuals
    }

    override fun toString(): String {
        return "<" + getName() + ">"
    }
}
