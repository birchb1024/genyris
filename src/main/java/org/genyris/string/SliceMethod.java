// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import java.math.BigDecimal;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class SliceMethod extends AbstractStringMethod {

	public static String getStaticName() {return Constants.SLICE;};
	
	public SliceMethod(Interpreter interp) {
        super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		BigDecimal start;
		BigDecimal end;
		StrinG theString = getSelfString(env);
		if(arguments.length == 2) {
			Class[] types = {Bignum.class, Bignum.class};
			checkArgumentTypes(types, arguments);
			start = ((Bignum)arguments[0]).bigDecimalValue();
			end = ((Bignum)arguments[1]).bigDecimalValue();
			return theString.slice(start, end);
		}
		if(arguments.length == 1) {
			Class[] types = {Bignum.class};
			checkArgumentTypes(types, arguments);
			start = ((Bignum)arguments[0]).bigDecimalValue();
			end = new BigDecimal(this.getSelfString(env).length(NIL));
			return theString.slice(start, end);
		}
		throw new GenyrisException("slice: invalid arguments length");
	}
}
