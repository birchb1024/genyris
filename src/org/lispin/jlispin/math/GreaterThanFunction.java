package org.lispin.jlispin.math;

import java.math.BigDecimal;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;

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
