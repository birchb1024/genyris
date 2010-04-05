package org.genyris.java;

import java.lang.reflect.Constructor;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
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
					.newInstance(toJavaArray(params, arguments)));
			result.addClass(genyrisClass);
			return result;
		} catch (Exception e) {
			throw new GenyrisException("Java "
					+ e.getCause().getClass().getName() + " "
					+ e.getCause().getMessage());
		}
	}

	private Object[] toJavaArray(Class[] params, Exp[] arguments) {
		Object[] result = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			result[i] = convertToJava(arguments[i]);
		}
		return result;
	}

	private Object convertToJava(Exp exp) {
		// TODO use visitor
		if (exp instanceof JavaWrapper) {
			return ((JavaWrapper) exp).getValue();
		} else if (exp instanceof Bignum) {
			return new Integer(((Bignum) exp).bigDecimalValue().intValue());
		} else if (exp instanceof StrinG) {
			return exp.toString();
		} else if (exp instanceof Symbol) {
			return exp.toString();
		} else
			return exp.toString();
	}

}
