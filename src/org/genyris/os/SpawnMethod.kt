// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.java.JavaWrapper
import java.io.IOException

class SpawnMethod(interp: Interpreter?) : AbstractMethod(interp, "spawn") {
    @kotlin.Throws(GenyrisException::class)
    private fun toStringArray(expArray: Array<Exp?>): Array<String?> {
        val result = arrayOfNulls<String>(expArray.size)
        for (i in expArray.indices) {
            if (expArray[i] !is StrinG) {
                throw GenyrisException(Constants.EXEC + " Non-string: " + expArray[i])
            } else {
                result[i] = (expArray[i] as StrinG).toString()
            }
        }
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        val child: Process?
        val args = toStringArray(arguments)
        try {
            child = Runtime.getRuntime().exec(args)
        } catch (e: IOException) {
            throw GenyrisException(
                "exec failed, message is: "
                        + e.getMessage()
            )
        }
        return JavaWrapper(child)
    }


    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.OS, SpawnMethod(interpreter))
        }
    }
}
