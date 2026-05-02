package org.genyris.task

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException

abstract class TaskFunction(interp: Interpreter, name: String?, eager: Boolean) : ApplicableFunction(
    interp, PrefixSymbol(
        Constants.PREFIX_TASK, name, "task"
    ), eager
) {
    @kotlin.Throws(GenyrisException::class)
    protected fun getThreadAsDictionary(tr: Thread, env: Environment): Dictionary {
        val result = Dictionary(env)
        result.addProperty(env, "state", StrinG(tr.getState().toString()))
        result.addProperty(env, "name", StrinG(tr.getName()))
        result.addProperty(env, "id", Bignum(tr.threadId().toDouble()))
        return result
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(SleepFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SpawnFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(KillTaskFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SpawnHTTPDFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ListTaskFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(CurrentTaskFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(SynchronizeFunction(interpreter))
        }
    }
}