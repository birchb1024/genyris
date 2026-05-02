package org.genyris.logic

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter

abstract class AbstractLogicFunction(interp: Interpreter, name: String?, eager: Boolean) :
    ApplicableFunction(interp, name, eager) {
    @kotlin.Throws(GenyrisException::class)
    abstract override fun bindAndExecute(
        proc: Closure?,
        arguments: Array<Exp?>?,
        envForBindOperations: Environment?
    ): Exp?

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(AndFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(NotFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(OrFunction(interpreter))
        }
    }
}