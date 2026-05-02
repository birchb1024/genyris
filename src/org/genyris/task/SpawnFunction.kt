// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.PrefixSymbol
import org.genyris.exception.GenyrisException
import org.genyris.exception.GenyrisInterruptedException
import org.genyris.interp.ClassicReadEvalPrintLoop
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.load.SourceLoader
import java.io.PrintWriter
import java.io.Writer

class SpawnFunction(interp: Interpreter?) : TaskFunction(interp, "spawn", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        checkMinArguments(arguments, 1)
        val task = BackGroundInterpreter(arguments)
        val thread = Thread(task)
        thread.setName(arrayOfExpToString(arguments))
        val result: Exp? = getThreadAsDictionary(thread, envForBindOperations)
        thread.start()
        return result
    }


    class BackGroundInterpreter(private val arguments: Array<Exp?>) : Runnable {
        override fun run() {
            val interpreter: Interpreter?
            try {
                if (arguments.size != 0) {
                    val filename: String? = arguments[0].toString()
                    interpreter = Interpreter()
                    interpreter.init(false, ClassicReadEvalPrintLoop.Companion.getContainingDirectoryPath(filename))
                    val output: Writer = PrintWriter(System.out)
                    val argv = interpreter.intern(PrefixSymbol(Constants.GENYRIS + "system#", Constants.ARGV, "sys"))
                    interpreter.getGlobalEnv().defineVariable(argv, arrayToExpList(interpreter.NIL, arguments))
                    SourceLoader.loadScriptFromFile(
                        interpreter.getGlobalEnv(),
                        interpreter.getSymbolTable(),
                        filename,
                        output
                    )
                }
            } catch (e: GenyrisException) {
                if (e is GenyrisInterruptedException) {
                    System.out.println(
                        "*** GenyrisInterruptedException " + Thread.currentThread().getName() + ' ' + e.getMessage()
                    )
                    Thread.currentThread().interrupt()
                    return
                }
                System.out.println("*** Error in thread " + Thread.currentThread().getName() + ' ' + e.getMessage())
            }
        }
    }

    companion object {
        fun arrayToExpList(NIL: Exp?, array: Array<Exp?>): Exp? {
            var result = NIL
            for (i in array.indices.reversed()) {
                result = Pair(array[i], result)
            }
            return result
        }

        fun arrayOfExpToString(array: Array<Exp?>): String {
            var result = ""
            for (i in array.indices) {
                result += (if (i > 0) " " else "") + array[i].toString()
            }
            return result
        }
    }
}
