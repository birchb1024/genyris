package org.lispin.jlispin.logic;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class AndFunction extends ApplicableFunction {

	private Exp NIL;
	
	public AndFunction(Exp nil) {
		NIL = nil;
	}
	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		if (arguments.length < 2)
			throw new LispinException("Too few arguments to and: " + arguments.length);

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] == NIL) {
                return NIL;
			}
		}
		return SymbolTable.T;

	}

}
