// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import java.math.BigDecimal;

import org.genyris.core.Exp;
import org.genyris.interp.Interpreter;

public class GreaterThanFunction extends AbstractMathBooleanFunction {

	public GreaterThanFunction(Interpreter interp) {
		super(interp, ">");
	}

	protected Exp mathOperation(Exp a, Exp b) {
		if (((BigDecimal) a.getJavaValue()).compareTo((BigDecimal) b
				.getJavaValue()) > 0) {
			return TRUE;
		} else {
			return NIL;
		}
	}
}
