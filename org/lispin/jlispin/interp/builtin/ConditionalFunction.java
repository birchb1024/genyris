package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.Procedure;

public class ConditionalFunction extends ApplicableFunction {

	public Exp apply(Procedure proc, Environment env, Exp[] arguments) throws LispinException {

		for(int i= 0; i < arguments.length; i++) {
			Exp condition = env.eval(arguments[i].car()); // TODO check if it exists?
			if( condition != SymbolTable.NIL ) {
				return env.evalSequence(arguments[i].cdr()); // TODO check if it exists?
			}
		}
		return SymbolTable.NIL;
	}
	
	public Object getJavaValue() {
		return "<the cond builtin function>";
	}

}
