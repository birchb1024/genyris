package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.Procedure;

public class EqualsFunction extends ApplicableFunction {

	public Exp bindAndExecute(Procedure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Too few arguments to EqFunction: " + arguments.length);
		if( arguments[0].equals(arguments[1]) )
			return SymbolTable.T;
		else
			return SymbolTable.NIL;			
	}

}
