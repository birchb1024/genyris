package org.genyris.java;


import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ImportMethod extends AbstractJavaMethod {

	public ImportMethod(Interpreter interp) {
		super(interp, "import");
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		Class[] types = { StrinG.class };
		this.checkArgumentTypes(types, arguments);
		String klassName = arguments[0].toString();
		
		return JavaUtils.importJavaClass(_interp, envForBindOperations, klassName);
	}

}
