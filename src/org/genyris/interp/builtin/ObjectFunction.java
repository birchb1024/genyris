package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class ObjectFunction extends ApplicableFunction {
	// Create a new dict

	public ObjectFunction(Interpreter interp) {
		super(interp);
	}


	public Exp bindAndExecute(Closure ignored, Exp[] arguments, Environment env) throws LispinException {
		Lobject dict = new Lobject(env);
		for(int i= 0; i < arguments.length; i++) {
			if( !arguments[i].listp())
				throw new LispinException("argument to dict not a list");
			dict.defineVariable(arguments[i].car(), Evaluator.eval(env, arguments[i].cdr())); 
		}
		return dict;
	}
}
