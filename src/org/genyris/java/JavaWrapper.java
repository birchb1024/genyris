// Copyright 2010 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.java;

import java.lang.reflect.Field;

import org.genyris.core.Atom;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class JavaWrapper extends Atom {
	private Object _value;

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.JAVAWRAPPER();
	}

	public JavaWrapper(Object i) {
		_value = i;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitJavaWrapper(this);
	}

	public String toString() {
		return _value.toString();
	}

	public int hashCode() {
		return _value.hashCode();
	}

	public boolean equals(Object compare) {
		if (compare == null)
			return false;
		if (compare.getClass() != this.getClass())
			return false;
		else
			return _value.equals(((JavaWrapper) compare)._value);
	}

	public Exp eval(Environment env) {
		return this;
	}

	public Object getValue() {
		return _value;
	}

	public Environment makeEnvironment(Environment parent)
			throws GenyrisException {
		return new JavaWrapperEnvironment(parent, this);
	}

	public void setField(String fieldName, Exp value, Environment env)
			throws GenyrisException {
		try {
			Field field = _value.getClass().getField(fieldName);
			Object converted = JavaUtils.convertToJava(field.getType(), value, env);
			field.setAccessible(true);
			field.set(_value, converted);
		} catch (SecurityException e) {
			throw new UnboundException(e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new UnboundException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnboundException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new UnboundException(e.getMessage());
		}

	}

	public Exp dir(Internable table) {
		Field[] fields = _value.getClass().getFields();
		Exp retval = Pair.cons4(new DynamicSymbol(table.SELF()),
				new DynamicSymbol(table.VARS()),
				new DynamicSymbol(table.CLASSES()), 
				new DynamicSymbol(table.JAVACLASS()), table.NIL());
		for (int i = fields.length - 1; i >= 0; i--) {
			retval = new Pair(new DynamicSymbol(table.internString(fields[i].getName())), retval);
		}
		return retval;
	}

	public Exp getField(Environment env, Symbol sym) throws UnboundException {
		try {
			Field field = _value.getClass().getField(sym.toString());
			field.setAccessible(true);
			return JavaUtils.javaToGenyris(env, field.get(_value));
		} catch (SecurityException e) {
			throw new UnboundException(e.getMessage());
		} catch (NoSuchFieldException e) {
			throw new UnboundException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new UnboundException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new UnboundException(e.getMessage());
		}
	}

	public boolean hasField(Symbol sym) {
		try {
			_value.getClass().getField(sym.toString());
			return true;
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}
    @Override
    public int compareTo(Object o) {
        return this == o ? 0 : 1;
    }

}


