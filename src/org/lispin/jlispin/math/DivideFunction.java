package org.lispin.jlispin.math;

import java.math.BigDecimal;
import java.math.MathContext;

import org.lispin.jlispin.core.Bignum;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class DivideFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length < 2)
			throw new LispinException("Too few arguments to /: " + arguments.length);
		try {
			Exp result = arguments[0];
			for( int i=1; i< arguments.length; i++ ) {
				result = divAux(result, arguments[i]);
			}
			return result;
		}
		catch(RuntimeException e) {
			throw new LispinException(e.getMessage());
		}
	}

	private Exp divAux(Exp a, Exp b) {
		// TODO make plus work for combiations of int, double and BigDecimal
		return new Bignum(((BigDecimal) a.getJavaValue()).divide((BigDecimal) b.getJavaValue(), new MathContext(10)));
	}
}
