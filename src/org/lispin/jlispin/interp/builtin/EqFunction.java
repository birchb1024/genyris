package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class EqFunction extends ApplicableFunction {
	Exp NIL;
	public EqFunction(Exp nil) {
		NIL = nil;		
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Too few arguments to EqualsFunction: " + arguments.length);
		if( arguments[0] == arguments[1] )
			return SymbolTable.T;
		else
			return NIL;			
	}

}
