package org.genyris.java;


import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ImportFunction extends ApplicableFunction {

	public ImportFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_JAVA + "import", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment env) throws GenyrisException {
		String javaClassName = arguments[0].eval(env).toString();
		String genyrisClassName = javaClassName.substring(1+javaClassName.lastIndexOf('.'));
		if( arguments.length == 3) {
			if(!arguments[1].toString().equals("as")) {
				throw new GenyrisException("java import missing 'as'.");
			}
			genyrisClassName = arguments[2].toString();
		}
		
		return JavaUtils.importJavaClass(_interp, genyrisClassName, env, javaClassName);
	}

}
