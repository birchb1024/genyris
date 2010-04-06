package org.genyris.java;

import java.lang.reflect.Constructor;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StandardClass;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class JavaCtor extends AbstractJavaMethod {

	private Constructor method;
	private Class[] params;
	private StandardClass genyrisClass;

	public JavaCtor(Interpreter interp, StandardClass glass, String name,
			Constructor method, Class[] params) throws GenyrisException {
		super(interp, name);
		this.method = method;
		this.params = params;
		this.genyrisClass = glass;
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.JAVACTOR();
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		try {
			JavaWrapper result = new JavaWrapper(method
					.newInstance(JavaUtils.toJavaArray(params, arguments, NIL)));
			result.addClass(genyrisClass);
			return result;
		} catch (Exception e) {
			throw new GenyrisException("Java "
					+ e.getCause().getClass().getName() + " "
					+ e.getCause().getMessage());
		}
	}
}
