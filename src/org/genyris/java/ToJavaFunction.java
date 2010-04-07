package org.genyris.java;


import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ToJavaFunction extends ApplicableFunction {

	public ToJavaFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_JAVA + "toJava", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment env) throws GenyrisException {
		Class[] types = { StrinG.class };
		checkArgumentTypes(types, arguments);
		String klassName = ((StrinG)arguments[0]).toString();
		Class klass;
		try {
			klass = Class.forName(klassName);
		} catch (ClassNotFoundException e) {
			throw new GenyrisException("ClassNotFoundException: " + e.getMessage());
		}
		
		return JavaUtils.wrapJavaObject(env, JavaUtils.convertToJava(klass, arguments[1], NIL));
	}
}
