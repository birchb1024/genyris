// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.format.Formatter
import org.genyris.format.IndentedFormatter
import org.genyris.io.*
import java.io.IOException
import java.io.Writer

object Runner {
    @kotlin.Throws(GenyrisException::class)
    fun executeScript(interp: Interpreter, stream: StringInStream?, output: Writer): Exp? {
        val input: InStream = UngettableInStream(
            ConvertEofInStream(
                IndentStream(
                    UngettableInStream(stream),
                    false
                )
            )
        )
        val parser = interp.newParser(input)
        val formatter: Formatter = IndentedFormatter(output, 3)
        var expression: Exp? = null
        var result: Exp? = null
        do {
            expression = parser.read()
            if (expression == interp.getSymbolTable().EOF()) {
                break
            }
            result = interp.evalInGlobalEnvironment(expression)
            result.acceptVisitor(formatter)
            try {
                output.flush()
            } catch (ignore: IOException) {
            }
        } while (true)
        return result
    }
}
