package org.genyris.java;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.EscapedSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class JavaUtils {
	public static Exp javaToGenyris(Environment env, Object rawResult) {
		Exp NIL = env.getNil();
		if ((rawResult == null) || (rawResult instanceof Void)) {
			return NIL;
		} else if (rawResult instanceof Boolean) {
			return ((Boolean) rawResult).booleanValue() ? env.getSymbolTable()
					.TRUE() : NIL;
		} else if (rawResult instanceof Character) {
			return new StrinG(((Character) rawResult).toString());
		} else if (rawResult instanceof String) {
			return new StrinG((String) rawResult);
		} else if (rawResult instanceof Integer) { // TODO move this into a
			// factory in Bignum?
			return new Bignum((Integer) rawResult);
		} else if (rawResult instanceof Long) {
			return new Bignum((Long) rawResult);
		} else if (rawResult instanceof Double) {
			return new Bignum((Double) rawResult);
		} else if (rawResult instanceof Float) {
			return new Bignum((Float) rawResult);
		} else if (rawResult instanceof Short) {
			return new Bignum((Short) rawResult);
		} else if (rawResult instanceof Byte) {
			return new Bignum((Byte) rawResult);
		} else if (rawResult instanceof BigDecimal) {
			return new Bignum((BigDecimal) rawResult);
		} else if (rawResult.getClass().isArray()) {
			Exp retval = NIL;
			int last = Array.getLength(rawResult) - 1;
			for (int i = last; i >= 0; i--) {
				retval = new Pair(javaToGenyris(env, Array.get(rawResult, i)),
						retval);
			}
			return retval;
		}
		JavaWrapper result = new JavaWrapper(rawResult);
		try {
			Class resultClass = rawResult.getClass();
			Exp klass = env.lookupVariableValue(env.internString(resultClass
					.getName()));
			result.addClass((Dictionary) klass);
		} catch (UnboundException e) {
			;
		}
		return result;
	}

	public static Object[] toJavaArray(Class[] params, Exp[] arguments, Symbol NIL) throws GenyrisException {
		if(params.length == 0)
			return null;
		Object[] result = new Object[params.length];
		if(params.length != arguments.length) {
			throw new GenyrisException("toJavaArray: missmatced lengths!");
		}
		for (int i = 0; i < params.length; i++) {
			result[i] = convertToJava(params[i], arguments[i], NIL);
		}
		return result;
	}

	public static Object convertToJava(Class klass, Exp exp, Symbol NIL) throws GenyrisException {
		if (klass == java.lang.Void.TYPE) {
			return null;
		} else if (exp instanceof JavaWrapper) {
			return ((JavaWrapper) exp).getValue();
		} else if (klass == java.lang.Boolean.TYPE ||klass == java.lang.Boolean.class) {
			return new java.lang.Boolean(exp == NIL ? false : true);
		} else if (exp instanceof Bignum) {
			BigDecimal big = ((Bignum) exp).bigDecimalValue();
			if (klass == java.lang.Byte.TYPE|| klass == java.lang.Byte.class) {
				return new java.lang.Byte(big.byteValue());
			} else if (klass == java.lang.Short.TYPE || klass == java.lang.Short.class) {
				return new java.lang.Short(big.shortValue());
			} else if (klass == java.lang.Integer.TYPE || klass == java.lang.Integer.class){
				return new java.lang.Integer(big.intValue());
			} else if (klass == java.lang.Long.TYPE || klass == java.lang.Long.class) {
				return new java.lang.Long(big.longValue());
			} else if (klass == java.lang.Float.TYPE || klass == java.lang.Float.class) {
				return new java.lang.Float(big.floatValue());
			} else if (klass == java.lang.Double.TYPE || klass == java.lang.Double.class) {
				return new java.lang.Double(big.doubleValue());
			} else {
				return big;
			}
		} else if ((exp instanceof StrinG) || (exp instanceof Symbol)) {
			if (klass == java.lang.Character.TYPE || klass == java.lang.Character.class) {
				return new java.lang.Character(exp.toString().charAt(0));
			} else
				return exp.toString();
		} else if (klass.isArray()) {
			if( !(exp instanceof Pair)) {
				throw new UnboundException("convertToJava: was expecting a list.");
			}
			int length = exp.length(NIL);
			Class elementType = klass.getComponentType();
			Object[] result = (Object[])Array.newInstance(elementType, length);
			for (int i = 0; i < length; i++) {
				result[i] = convertToJava(elementType, exp.car(), NIL);
				exp = exp.cdr();
			}
			return result;

		} else
			return exp.toString();
	}

	public static Exp importJavaClass(Interpreter interp, Environment env,
			String klassName) throws GenyrisException {
		Class klass = null;
		try {
			klass = Class.forName(klassName);
		} catch (ClassNotFoundException e) {
			throw new GenyrisException("Java ClassNotFoundException: "
					+ e.getMessage());
		}
		StandardClass glass = StandardClass.makeClass(interp.getGlobalEnv(),
				interp.intern(new EscapedSymbol(klassName)), new Pair(interp
						.intern(Constants.JAVA), interp.NIL));
		//
		Constructor[] ctors = klass.getConstructors();
		for (int c = 0; c < ctors.length; c++) {
			Class[] params = ctors[c].getParameterTypes();
			String name = "new";
			for (int p = 0; p < params.length; p++) {
				name += "-" + params[p].getName();
			}
			JavaCtor gctor = new JavaCtor(interp, glass, name, ctors[c], params);
			glass.defineDynamicVariable(new DynamicSymbol(interp.intern(name)),
					new EagerProcedure(env, interp.NIL, gctor));
		}
		//
		Method[] methods = klass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Class[] params = methods[i].getParameterTypes();
			String name = methods[i].getName();
			for (int p = 0; p < params.length; p++) {
				name += "-" + params[p].getName();
			}
			ApplicableFunction gmethod;
			if (Modifier.isStatic(methods[i].getModifiers())) {
				gmethod = new JavaStaticMethod(interp, name, methods[i], params);
			} else {
				gmethod = new JavaMethod(interp, name, methods[i], params);
			}
		    SimpleSymbol sym = interp.intern(name);
			glass.defineDynamicVariable(new DynamicSymbol(sym),
					new EagerProcedure(env, interp.NIL, gmethod));

		}
		return glass;
	}

	public static Object  convertToJava(Exp exp, Symbol NIL) throws GenyrisException{
		return convertToJava(java.lang.Object.class,  exp,  NIL);
	}

}
