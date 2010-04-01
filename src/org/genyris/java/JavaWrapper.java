// Copyright 2010 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.java;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

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
		if (compare.getClass() != this.getClass())
			return false;
		else
			return _value.equals(((JavaWrapper)compare)._value);
	}
	public Exp eval(Environment env) {
		return this;
	}

	public Object getValue() {
		return _value;
	}
}
