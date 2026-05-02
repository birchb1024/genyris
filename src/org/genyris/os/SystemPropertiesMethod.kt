// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os

import org.genyris.core.*
import org.genyris.core.Dictionary
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import java.util.*

class SystemPropertiesMethod(interp: Interpreter?) : AbstractMethod(interp, "getProperties") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp {
        return convertPropertiesToDictionary(env, System.getProperties())
    }

    @kotlin.Throws(GenyrisException::class)
    fun convertPropertiesToDictionary(env: Environment, props: Properties): Dictionary {
        val retval = Dictionary(env)
        val iter = props.propertyNames()
        while (iter.hasMoreElements()) {
            val key = iter.nextElement() as String?
            retval.defineVariableRaw(
                env.getSymbolTable().internSymbol(EscapedSymbol(key)),
                StrinG(props.getProperty(key))
            )
        }
        return retval
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.OS, SystemPropertiesMethod(interpreter))
        }
    }
}
