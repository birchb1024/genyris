package org.genyris.format

import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Interpreter

abstract class AbstractFormatFunction(interp: Interpreter, name: String?, eager: Boolean) :
    ApplicableFunction(interp, name, eager) {
    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(PrintFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(WriteFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DisplayFunction(interpreter))
        }
    }
}