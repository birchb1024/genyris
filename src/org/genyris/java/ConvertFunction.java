package org.genyris.java;


import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ConvertFunction extends ApplicableFunction {

	public ConvertFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_JAVA + "convert", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		if(arguments.length == 1) {
			return new JavaWrapper(JavaUtils.convertToJava(arguments[0], NIL));
		}
		Class[] types = { Symbol.class };
		checkArgumentTypes(types, arguments);
		String klassName = ((Symbol)arguments[0]).getPrintName();
		Class klass;
		try {
			klass = Class.forName(klassName);
		} catch (ClassNotFoundException e) {
			throw new GenyrisException("ClassNotFoundException: " + e.getMessage());
		}
		
		return new JavaWrapper(JavaUtils.convertToJava(klass, arguments[1], NIL));
	}
}
