package org.genyris.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
			Environment env) throws GenyrisException {
		Object[] javaArgsArray = JavaUtils.toJavaArray(params, arguments, env);
		try {
			JavaWrapper result = new JavaWrapper(method
					.newInstance(javaArgsArray));
			result.addClass(genyrisClass);
			return result;
		} catch (IllegalArgumentException e) {
			throw new GenyrisException("Java " + e.getClass().getName() + " "
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			throw new GenyrisException("Java " + e.getClass().getName() + " "
					+ e.getMessage());
		} catch (InstantiationException e) {
			throw new GenyrisException("Java " + e.getClass().getName() + " "
					+ e.getMessage());
		} catch (InvocationTargetException e) {
			throw new GenyrisException("Java "
					+ e.getCause().getClass().getName() + " "
					+ e.getCause().getMessage());
		}
	}
}
