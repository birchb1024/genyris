package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class DefineFunction extends ApplicableFunction {

	public DefineFunction(Interpreter interp) {
		super(interp);
	}
	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		if( arguments.length != 2) throw new LispinException("Incorrect number of arguments to set.");
		env.defineVariable(arguments[0], arguments[1]);
		
		return arguments[1];
	}
	public Object getJavaValue() {
		return "<the defvar builtin function>";
	}

}