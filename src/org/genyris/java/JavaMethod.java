package org.genyris.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

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
		Object object = getSelfJava(envForBindOperations).getValue();
		Object[] javaArgsArray = JavaUtils.toJavaArray(params, arguments, envForBindOperations);
		try {
			method.setAccessible(true);
			Object rawResult = method.invoke(object, javaArgsArray);
			return JavaUtils.javaToGenyris(envForBindOperations, rawResult);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new GenyrisException("Java " + this + " " + e.getClass().getName() + " "
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new GenyrisException("Java " + this + " " + e.getClass().getName() + " "
					+ e.getMessage());
		} catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
			throw new GenyrisException("Java " + this + " " 
					+ e.getCause().getClass().getName() + " "
					+ e.getCause().getMessage());
		}
	}
    public String toString() {
        return "JavaMethod " + getName();
    }

}
