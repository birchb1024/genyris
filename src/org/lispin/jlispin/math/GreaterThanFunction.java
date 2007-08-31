package org.lispin.jlispin.math;

import java.math.BigDecimal;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class GreaterThanFunction extends ApplicableFunction {
	
	public GreaterThanFunction(Interpreter interp) {
		super(interp);
	}


	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Not two arguments to >: " + arguments.length);
		try {
			return gtAux(arguments[0], arguments[1]);
		}
		catch(RuntimeException e) {
			throw new LispinException(e.getMessage());
		}
	}

	private Exp gtAux(Exp a, Exp b) {
		// TODO make plus work for combiations of int, double and BigDecimal
		if ( ((BigDecimal) a.getJavaValue()).compareTo((BigDecimal) b.getJavaValue()) > 0 ) {
			return TRUE;
		}
		else {
			return NIL;
		}
	}
}
