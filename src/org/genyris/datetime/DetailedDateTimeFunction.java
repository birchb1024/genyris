package org.genyris.datetime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class DetailedDateTimeFunction extends AbstractDateTimeFunction {

    public DetailedDateTimeFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_DATE + "calendar");
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
		TimeZone tz;
		checkMinArguments(arguments, 1);
		if (arguments.length == 2) {
			tz = TimeZone.getTimeZone(arguments[2].toString());
		} else {
			tz = TimeZone.getDefault();
		}
		GregorianCalendar cal = new GregorianCalendar(tz);
		cal.setTimeInMillis(((Bignum)arguments[0]).bigDecimalValue().longValue());
		Dictionary result = new Dictionary(env);
		result.defineDynamicVariable(new DynamicSymbol((SimpleSymbol) env.internString("era")), new Bignum(cal.get(Calendar.ERA)));
		result.addProperty(env,"era", cal.get(Calendar.ERA) == GregorianCalendar.BC ? env.internString("BC") : env.internString("AD"));
		result.defineInt("year", cal.get(Calendar.YEAR));
		result.addProperty(env,"leap-year", cal.isLeapYear(cal.get(Calendar.YEAR))? TRUE : NIL);
		result.defineInt("month", cal.get(Calendar.MONTH));
		result.defineInt("week-of-year", cal.get(Calendar.WEEK_OF_YEAR));
		result.defineInt("week-of-month", cal.get(Calendar.WEEK_OF_MONTH));
		result.defineInt("day-of-month", cal.get(Calendar.DAY_OF_MONTH));
		result.defineInt("day-of-year", cal.get(Calendar.DAY_OF_YEAR));
		result.defineInt("day-of-week", cal.get(Calendar.DAY_OF_WEEK));
		result.defineInt("day-of-week-in-month", cal.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		result.addProperty(env,"am-pm", cal.get(Calendar.AM_PM) == Calendar.AM ? env.internString("am") : env.internString("pm"));
		result.defineInt("hour", cal.get(Calendar.HOUR));
		result.defineInt("hour-of-day", cal.get(Calendar.HOUR_OF_DAY));
		result.defineInt("minute", cal.get(Calendar.MINUTE));
		result.defineInt("second", cal.get(Calendar.SECOND));
		result.defineInt("millisecond", cal.get(Calendar.MILLISECOND));
		result.defineInt("zone-offset", cal.get(Calendar.ZONE_OFFSET)/(60*60*1000));
		result.defineInt("dst-offset", cal.get(Calendar.DST_OFFSET));
		
		return result;
	}
}
