package org.genyris.datetime

import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.text.SimpleDateFormat
import java.util.*

class FormatDateFunction(interp: Interpreter?) : AbstractDateTimeFunction(interp, "format-date") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp {
        val tz: TimeZone?

        checkArguments(arguments, 3)
        val types = arrayOf<Class<*>?>(Bignum::class.java, StrinG::class.java, StrinG::class.java)
        checkArgumentTypes(types, arguments)

        if (arguments.size == 2) {
            tz = TimeZone.getDefault()
        } else {
            tz = TimeZone.getTimeZone(arguments[2].toString())
        }
        val `in`: Date = Date((arguments[0] as Bignum).bigDecimalValue().longValue())
        try {
            val df = SimpleDateFormat(arguments[1].toString())
            df.setTimeZone(tz)
            return StrinG(df.format(`in`))
        } catch (e: Exception) {
            throw GenyrisException(e.getMessage())
        }
    }
}
