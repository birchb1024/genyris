package org.genyris.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class FormatDateFunction extends AbstractDateTimeFunction {

    public FormatDateFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_DATE + "format-date");
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
		TimeZone tz;

		checkArguments(arguments, 3);
		Class[] types = { Bignum.class, StrinG.class, StrinG.class };
		checkArgumentTypes(types, arguments);

		if (arguments.length == 2) {
			tz = TimeZone.getDefault();
		} else {
			tz = TimeZone.getTimeZone(arguments[2].toString());
		}
		Date in = new Date(((Bignum)arguments[0]).bigDecimalValue().longValue());
		try {
			SimpleDateFormat df = new SimpleDateFormat(arguments[1].toString());
			df.setTimeZone(tz);
			return new StrinG(df.format(in));
		}
		catch (Exception e) {
			throw new GenyrisException(e.getMessage());
		}
	}
}
