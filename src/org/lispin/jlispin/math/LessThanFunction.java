package org.lispin.jlispin.math;

import java.math.BigDecimal;

import org.genyris.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class LessThanFunction extends ApplicableFunction {

	public LessThanFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Not two arguments to < " + arguments.length);
		try {
			return ltAux(arguments[0], arguments[1]);
		}
		catch(RuntimeException e) {
			throw new LispinException(e.getMessage());
		}
	}

	private Exp ltAux(Exp a, Exp b) {
		// TODO make plus work for combiations of int, double and BigDecimal
		if ( ((BigDecimal) a.getJavaValue()).compareTo((BigDecimal) b.getJavaValue()) < 0 ) {
			return TRUE;
		}
		else {
			return NIL;
		}
	}
}
