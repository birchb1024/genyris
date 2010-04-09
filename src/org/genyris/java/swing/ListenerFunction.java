package org.genyris.java.swing;


import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.java.JavaWrapper;

public class ListenerFunction extends ApplicableFunction {

	public ListenerFunction(Interpreter interp) {
		super(interp, Constants.PREFIX_JAVA + "actionListener", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment env) throws GenyrisException {
		Class[] types = {Closure.class};
		this.checkArgumentTypes(types, arguments);
		return new JavaWrapper( new GenyrisActionListener((Closure)arguments[0], env));		
	}

}
