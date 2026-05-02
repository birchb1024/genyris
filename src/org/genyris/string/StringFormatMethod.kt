// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.io.writerstream.WriterStream
import java.io.StringWriter
import java.io.Writer

class StringFormatMethod(interp: Interpreter?) : AbstractStringMethod(
    interp,
    staticName
) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
        val theString = env.getSelf()
        if (arguments.size == 0) {
            return theString
        }
        val out: Writer = StringWriter()
        val str = WriterStream(out)
        str.format(theString as StrinG?, 0, arguments, env)
        return StrinG(out.toString())
    }

    companion object {
        val staticName: String
            get() = Constants.FORMAT
    }
}
