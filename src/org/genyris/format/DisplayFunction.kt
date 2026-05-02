// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.io.IOException
import java.io.PrintWriter
import java.io.Writer

class DisplayFunction(interp: Interpreter?) : AbstractFormatFunction(interp, "display", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        val output: Writer = PrintWriter(System.out)
        val formatter: Formatter = DisplayFormatter(output)
        for (i in arguments.indices) {
            arguments[i]!!.acceptVisitor(formatter)
            try {
                output.flush()
            } catch (e: IOException) {
                throw GenyrisException(e.getMessage())
            }
        }
        return TRUE
    }
}
