package org.genyris.java;


import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ToGenyrisFunction extends ApplicableFunction {

	public ToGenyrisFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_JAVA + "toGenyris", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment env) throws GenyrisException {
		return JavaUtils.javaToGenyris(env, ((JavaWrapper)arguments[0]).getValue());
	}
}
