package org.genyris.java;

import java.lang.reflect.Method;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class JavaStaticMethod extends ApplicableFunction {
	protected Method method;
	protected Class[] params;

	public JavaStaticMethod(Interpreter interp, String name, Method method,
			Class[] params) throws GenyrisException {
		super(interp, name, true);
		this.method = method;
		this.params = params;
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.JAVASTATICMETHOD();
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		try {
			method.setAccessible(true);
			Object[] args = JavaUtils.toJavaArray(params, arguments, NIL);
			Object rawResult = method.invoke(null, args);
			return JavaUtils.javaToGenyris(env, rawResult);
		} catch (Exception e) {
			throw new GenyrisException("Java "
					+ e.getCause().getClass().getName() + " "
					+ e.getCause().getMessage());
		}
	}

}
