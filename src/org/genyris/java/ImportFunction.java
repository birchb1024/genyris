package org.genyris.java;


import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Symbol;
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
			Environment envForBindOperations) throws GenyrisException {
		Class[] types = { Symbol.class };
		this.checkArgumentTypes(types, arguments);
		String klassName = ((Symbol)arguments[0]).getPrintName();
		
		return JavaUtils.importJavaClass(_interp, envForBindOperations, klassName);
	}

}
