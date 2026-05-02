// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load

import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.io.NullWriter
import java.io.IOException
import java.io.Writer

class LoadFunction(interp: Interpreter) : ApplicableFunction(interp, "load", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        var result: Exp = NIL
        checkMinArguments(arguments, 1)
        try {
            if (arguments[0] !is StrinG) {
                throw GenyrisException("non-string file path argument passed to load: " + arguments[0].toString())
            }
            if (arguments.size > 1) {
                if (arguments[1] === TRUE) {
                    val out = _interp.getDefaultOutputWriter()
                    result = SourceLoader.loadScriptFromClasspath(
                        _interp.getGlobalEnv(),
                        _interp.getSymbolTable(), arguments[0].toString(), out
                    )
                    return result
                }
            }

            val out: Writer = NullWriter()
            result = SourceLoader.loadScriptFromClasspath(
                _interp.getGlobalEnv(),
                _interp.getSymbolTable(), arguments[0].toString(), out
            )
            out.close()
        } catch (unknown: IOException) {
            throw GenyrisException(unknown.getMessage())
        }
        return result
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(LoadFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(IncludeFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ImportFunction(interpreter))
        }
    }
}
