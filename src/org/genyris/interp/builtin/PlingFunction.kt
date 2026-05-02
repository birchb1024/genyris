// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.genyris.core.*
import org.genyris.dl.AbstractGraph
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

class PlingFunction(interp: Interpreter) : ApplicableFunction(interp, "pling", false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment): Exp? {
        val lhs = arguments[0].eval(env)
        var rhs = arguments[1]
        val E = lhs.makeEnvironment(env)

        if ((lhs is Bignum || lhs is StrinG || lhs is Triple || lhs is Dictionary) && rhs is SimpleSymbol) { // <dict>!w
            rhs = DynamicSymbol(rhs)
        }
        if (lhs is Pair) {
            when (rhs) {
                -> return lhs.makeEnvironment(env).lookupLexicalVariableValue(ss)
                -> return lhs.makeEnvironment(env).lookupDynamicVariableValue(ds)
                -> return lhs.nth(bn.bigDecimalValue().intValue(), NIL)
                else -> {}
            }
        }
        if (lhs is AbstractGraph && rhs is Symbol) {
            return lhs.shift(rhs, env)
        }
        val erhs = rhs.eval(E)
        if (lhs is Pair && erhs is Bignum) {
            val p = lhs
            return p.nth(erhs.bigDecimalValue().intValue(), NIL)
        }
        if (lhs is AbstractGraph && erhs is Symbol) {
            return lhs.shift(erhs, env)
        }
        return erhs
    }

    companion object {
        private val log: Log? = LogFactory.getLog(PlingFunction::class.java)
    }
}
