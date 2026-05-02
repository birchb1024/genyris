// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin

import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.SimpleSymbol
import org.genyris.core.StandardClass
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class TagFunction(interp: Interpreter) : ApplicableFunction(interp, "tag", true) {
    private fun VALIDATE(): SimpleSymbol? {
        return _interp.getSymbolTable().VALIDATE()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp>,
        environment: Environment
    ): Exp {
        checkArguments(arguments, 2)
        val types = arrayOf<Class<*>?>(StandardClass::class.java)
        checkArgumentTypes(types, arguments)
        val `object` = arguments[1]
        val klass = arguments[0] as StandardClass
        callValidator(environment, `object`, klass)
        `object`.addClass(klass)
        return `object`
    }

    // TODO move these into ClassWrapper:
    @kotlin.Throws(GenyrisException::class)
    fun callValidator(
        environment: Environment, `object`: Exp?,
        klassobject: StandardClass
    ) {
        // TODO DRY
        val NIL: Exp? = environment.getNil()
        var validator: Exp? = null
        try {
            validator = klassobject.lookupVariableValue(DynamicSymbol(VALIDATE()))
        } catch (ignore: UnboundException) {
            return
        }
        val args: Array<Exp?>? = arrayOfNulls<Exp>(1)
        args!![0] = `object`
        val result = validator.applyFunction(klassobject, args)
        if (result === NIL) {
            throw GenyrisException(
                ("class " + klassobject.getClassName()
                        + " validator error for object " + `object`)
            )
        }
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun validateObjectInClass(
            environment: Environment,
            `object`: Exp, klassobject: StandardClass
        ) {
            val NIL: Exp? = environment.getNil()
            var validator: Exp? = null
            try {
                validator = klassobject.lookupVariableValue(DynamicSymbol(VALIDATE(environment)))
            } catch (ignore: UnboundException) {
                if (!klassobject.isInstance(`object`)) {
                    throw GenyrisTypeMismatchException(
                        "validator error: object " + `object` +
                                " is not tagged with " + klassobject.getClassName()
                    )
                }
            }
            if (validator != null) {
                val args: Array<Exp?>? = arrayOfNulls<Exp>(1)
                args!![0] = `object`
                val result = validator.applyFunction(klassobject, args)
                if (result === NIL) {
                    throw GenyrisException(
                        ("class " + klassobject.getClassName()
                                + " validator error for object " + `object`)
                    )
                }
            }
        }

        private fun VALIDATE(environment: Environment): SimpleSymbol? {
            return environment.getSymbolTable().VALIDATE()
        }
    }
}
