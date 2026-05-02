// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.PrefixSymbol
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.io.NullWriter
import java.io.IOException
import java.io.Writer

class ImportFunction(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_SYSTEM, "import", "sys"), true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        var result: Exp = NIL
        try {
            var out: Writer = NullWriter()
            this.checkArguments(arguments, 1, 2)
            if (arguments[0] !is StrinG) {
                out.close()
                throw GenyrisException(
                    "non-String argument passed to sys:import: "
                            + arguments[0].toString()
                )
            }
            if (arguments.size > 1) {
                if (arguments[1] === TRUE) {
                    out.close()
                    out = _interp.getDefaultOutputWriter()
                }
            }
            result = SourceLoader.loadScriptFromFile(
                env, _interp.getSymbolTable(),
                arguments[0].toString(), out
            )
            out.close()
            return result
        } catch (unknown: IOException) {
            throw GenyrisException(unknown.getMessage())
        }
    }
}
