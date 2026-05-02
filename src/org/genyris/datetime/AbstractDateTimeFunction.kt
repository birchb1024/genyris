// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.datetime

import org.genyris.core.Constants
import org.genyris.core.PrefixSymbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Interpreter

abstract class AbstractDateTimeFunction(
    interp: Interpreter,
    name: String?
) : ApplicableFunction(interp, PrefixSymbol(Constants.GENYRIS + "date#", name, name), true) {
    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(FormatDateFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(DetailedDateTimeFunction(interpreter))
        }
    }
}