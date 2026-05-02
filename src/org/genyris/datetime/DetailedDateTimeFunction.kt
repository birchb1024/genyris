package org.genyris.datetime

import org.genyris.core.*
import org.genyris.core.Dictionary
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.util.*

class DetailedDateTimeFunction(interp: Interpreter?) : AbstractDateTimeFunction(interp, "calendar") {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp {
        val tz: TimeZone?
        checkMinArguments(arguments, 1)

        if (arguments.size == 2) {
            val types = arrayOf<Class<*>?>(Bignum::class.java, StrinG::class.java)
            checkArgumentTypes(types, arguments)
            tz = TimeZone.getTimeZone(arguments[1].toString())
        } else {
            val types = arrayOf<Class<*>?>(Bignum::class.java)
            checkArgumentTypes(types, arguments)
            tz = TimeZone.getDefault()
        }
        val cal = GregorianCalendar(tz)
        cal.setTimeInMillis((arguments[0] as Bignum).bigDecimalValue().longValue())
        val result = Dictionary(env)
        result.defineDynamicVariable(
            DynamicSymbol(env.internString("era") as SimpleSymbol?),
            Bignum(cal.get(Calendar.ERA))
        )
        result.addProperty(
            env,
            "era",
            if (cal.get(Calendar.ERA) == GregorianCalendar.BC) env.internString("BC") else env.internString("AD")
        )
        result.defineInt("year", cal.get(Calendar.YEAR))
        result.addProperty(env, "leap-year", if (cal.isLeapYear(cal.get(Calendar.YEAR))) TRUE else NIL)
        result.defineInt("month", cal.get(Calendar.MONTH))
        result.defineInt("week-of-year", cal.get(Calendar.WEEK_OF_YEAR))
        result.defineInt("week-of-month", cal.get(Calendar.WEEK_OF_MONTH))
        result.defineInt("day-of-month", cal.get(Calendar.DAY_OF_MONTH))
        result.defineInt("day-of-year", cal.get(Calendar.DAY_OF_YEAR))
        result.defineInt("day-of-week", cal.get(Calendar.DAY_OF_WEEK))
        result.defineInt("day-of-week-in-month", cal.get(Calendar.DAY_OF_WEEK_IN_MONTH))
        result.addProperty(
            env,
            "am-pm",
            if (cal.get(Calendar.AM_PM) == Calendar.AM) env.internString("am") else env.internString("pm")
        )
        result.defineInt("hour", cal.get(Calendar.HOUR))
        result.defineInt("hour-of-day", cal.get(Calendar.HOUR_OF_DAY))
        result.defineInt("minute", cal.get(Calendar.MINUTE))
        result.defineInt("second", cal.get(Calendar.SECOND))
        result.defineInt("millisecond", cal.get(Calendar.MILLISECOND))
        result.defineInt("zone-offset", cal.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000))
        result.defineInt("dst-offset", cal.get(Calendar.DST_OFFSET))

        return result
    }
}
