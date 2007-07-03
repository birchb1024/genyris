package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.CallableEnvironment;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.LispinException;

public class DictFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		Lobject dict = new Lobject();
		for(int i= 0; i < arguments.length; i++) {
			if( !arguments[i].listp())
				throw new LispinException("argument to dict not a list");
			if(arguments[i].cdr().listp())
				dict.defineVariable(arguments[i].car(), Evaluator.eval(env, arguments[i].cdr().car())); 
			else
				dict.defineVariable(arguments[i].car(), SymbolTable.NIL);
		}
		return new CallableEnvironment(dict);
	}
}
