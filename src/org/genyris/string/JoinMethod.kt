// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.format.DisplayFormatter
import org.genyris.format.Formatter
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.io.StringWriter

class JoinMethod(interp: Interpreter?) : AbstractStringMethod(
    interp,
    staticName
) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp>, env: Environment?): Exp {
        val output = StringWriter()
        val formatter: Formatter = DisplayFormatter(output)
        if (arguments[0] === NIL) {
            return StrinG("")
        }
        val types = arrayOf<Class<*>?>(Pair::class.java)
        checkArgumentTypes(types, arguments)
        val theString = getSelfString(env)
        var theList = arguments[0]
        while (NIL !== theList.cdr()) {
            theList.car().acceptVisitor(formatter)
            theString.acceptVisitor(formatter)
            theList = theList.cdr()
        }
        theList.car().acceptVisitor(formatter)
        return StrinG(output.toString())
    }

    companion object {
        val staticName: String
            get() = Constants.JOIN
    }
}
