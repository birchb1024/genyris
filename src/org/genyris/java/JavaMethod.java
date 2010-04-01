package org.genyris.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class JavaMethod extends AbstractJavaMethod {

	private Method method;
	private Class[] params;

	public JavaMethod(Interpreter interp, String name, Method method,
			Class[] params) throws GenyrisException {
		super(interp, name);
		this.method = method;
		this.params = params;
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		try {
			JavaWrapper result = 
				new JavaWrapper(method.invoke(getSelfJava(envForBindOperations).getValue(), toJavaArray(params, arguments)));
			return result;
		} catch (IllegalArgumentException e) {
			throw new GenyrisException("Java IllegalArgumentException " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new GenyrisException("Java IllegalAccessException " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new GenyrisException("Java InvocationTargetException " + e.getMessage());
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
		// TODO use visitor and desired java type
		if (exp instanceof JavaWrapper) {
			return ((JavaWrapper) exp).getValue();
		} else if (exp instanceof Bignum) {
			return new Integer(((Bignum) exp).bigDecimalValue().intValue());
		} else if (exp instanceof StrinG) {
			return exp.toString();
		} else if (exp instanceof Symbol) {
			return exp.toString();
		}else
			return exp.toString();
	}

}
