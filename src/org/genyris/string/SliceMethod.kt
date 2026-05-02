// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.Bignum
import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.math.BigDecimal

class SliceMethod(interp: Interpreter?) : AbstractStringMethod(
    interp,
    staticName
) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
        val start: BigDecimal?
        val end: BigDecimal?
        val theString = getSelfString(env)
        if (arguments.size == 2) {
            val types = arrayOf<Class<*>?>(Bignum::class.java, Bignum::class.java)
            checkArgumentTypes(types, arguments)
            start = (arguments[0] as Bignum).bigDecimalValue()
            end = (arguments[1] as Bignum).bigDecimalValue()
            return theString.slice(start, end)
        }
        if (arguments.size == 1) {
            val types = arrayOf<Class<*>?>(Bignum::class.java)
            checkArgumentTypes(types, arguments)
            start = (arguments[0] as Bignum).bigDecimalValue()
            end = BigDecimal(this.getSelfString(env).length(NIL))
            return theString.slice(start, end)
        }
        throw GenyrisException("slice: invalid arguments length")
    }

    companion object {
        val staticName: String
            get() = Constants.SLICE
    }
}
