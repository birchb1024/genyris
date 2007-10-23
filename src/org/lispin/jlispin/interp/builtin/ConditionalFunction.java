package org.lispin.jlispin.interp.builtin;

import org.genyris.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class ConditionalFunction extends ApplicableFunction {

	
	public ConditionalFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		for(int i= 0; i < arguments.length; i++) {
			Exp condition = Evaluator.eval(env, arguments[i].car()); // TODO check if it exists?
			if( condition != NIL ) {
				return Evaluator.evalSequence(env, arguments[i].cdr()); // TODO check if it exists?
			}
		}
		return NIL;
	}
	public Object getJavaValue() {
		return "<the cond builtin function>";
	}

}
