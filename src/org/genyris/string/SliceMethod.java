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
    	Class[] types = {Bignum.class, Bignum.class};
    	checkArgumentTypes(types, arguments);
		BigDecimal start = ((Bignum)arguments[0]).bigDecimalValue();
		BigDecimal end = ((Bignum)arguments[1]).bigDecimalValue();
		if(end.intValue() == -1) {
			end = new BigDecimal(this.getSelfString(env).length(NIL)-1);
		}
		StrinG theString = getSelfString(env);
		return theString.slice(start, end);
	}
}
