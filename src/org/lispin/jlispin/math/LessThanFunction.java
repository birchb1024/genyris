package org.lispin.jlispin.math;

import java.math.BigDecimal;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class LessThanFunction extends ApplicableFunction {

	private Exp NIL;
	
	public LessThanFunction(Exp nil) {
		NIL = nil;
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
			return SymbolTable.T;
		}
		else {
			return NIL;
		}
	}
}
