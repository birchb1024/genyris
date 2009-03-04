// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import java.math.BigDecimal;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.interp.Interpreter;

public class MultiplyFunction extends AbstractMathFunction {

	public static String getStaticName() {return "*";};
	public static boolean isEager() {return true;};
	
     public MultiplyFunction(Interpreter interp) {
        super(interp,  getStaticName(), 2);
    }

    protected Exp mathOperation(Exp a, Exp b) {
        return new Bignum(((BigDecimal) a.getJavaValue()).multiply((BigDecimal) b.getJavaValue()));
    }

	public String getName() {
		return getStaticName();
	}
}
