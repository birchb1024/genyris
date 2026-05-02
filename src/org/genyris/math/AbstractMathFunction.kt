// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

abstract class AbstractMathFunction(
    interp: Interpreter,
    name: String?, private val _minExpectedNumberOfArguments: Int
) : ApplicableFunction(interp, name, true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        if (arguments.size < _minExpectedNumberOfArguments) throw GenyrisException("Too few arguments to " + getName())
        try {
            if (arguments.size == 1) {
                return mathOperation(arguments[0])
            }
            var result = arguments[0]
            for (i in 1..<arguments.size) {
                result = mathOperation(result, arguments[i])
            }
            return result
        } catch (e: RuntimeException) {
            throw GenyrisException(e.getMessage())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    protected open fun mathOperation(unary: Exp?): Exp? {
        throw GenyrisException("Bad call to mathOperation(Exp unary)")
    }

    @kotlin.Throws(GenyrisException::class)
    protected open fun mathOperation(a: Exp?, b: Exp?): Exp? {
        throw GenyrisException("Bad call to mathOperation(Exp a, Exp b)")
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(DivideFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(GreaterThanFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(LessThanFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(MinusFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(MultiplyFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(PlusFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(PowerFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(RemainderFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ScaleFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SquareRootFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SinFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(CosFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(Atan2Function(interpreter))
        }
    }
}