package org.genyris.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.genyris.core.Bignum;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class JavaMethod extends AbstractJavaMethod {

	protected Method method;
	protected Class[] params;

	public JavaMethod(Interpreter interp, String name, Method method,
			Class[] params) throws GenyrisException {
		super(interp, name);
		this.method = method;
		this.params = params;
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.JAVAMETHOD();
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		try {
			Object rawResult = method.invoke(getSelfJava(envForBindOperations)
					.getValue(), toJavaArray(params, arguments));
			return reverseCoerce(envForBindOperations, rawResult);

		} catch (IllegalArgumentException e) {

			throw new GenyrisException("Java IllegalArgumentException "
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			throw new GenyrisException("Java IllegalAccessException "
					+ e.getMessage());
		} catch (InvocationTargetException e) {
			throw new GenyrisException("Java InvocationTargetException "
					+ e.getMessage());
		}
	}

	private Exp reverseCoerce(Environment envForBindOperations, Object rawResult) {
		if( rawResult instanceof String) {
			return new StrinG((String)rawResult);
		} else if ( rawResult instanceof Integer) { // TODO move this into factopry in Bignum?
			return new Bignum((Integer)rawResult);
		} else if ( rawResult instanceof Long) {
			return new Bignum((Long)rawResult);
		} else if ( rawResult instanceof Double) {
			return new Bignum((Double)rawResult);
		} else if ( rawResult instanceof Float) {
			return new Bignum((Float)rawResult);
		}
		JavaWrapper result = new JavaWrapper(rawResult);
		try {
			Class resultClass = rawResult.getClass();
			Exp klass = envForBindOperations
					.lookupVariableValue(envForBindOperations
							.internString(resultClass.getName()));
			result.addClass((Dictionary) klass);
		} catch (UnboundException e) {
			;
		}
		return result;
	}

	public static Object[] toJavaArray(Class[] params, Exp[] arguments) {
		Object[] result = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			result[i] = convertToJava(arguments[i]);
		}
		return result;
	}

	public static Object convertToJava(Exp exp) {
		// TODO use visitor and desired java type
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
