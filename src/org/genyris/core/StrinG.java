// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.regex.PatternSyntaxException;
import org.genyris.exception.GenyrisException;
import org.genyris.core.Bignum;

public class StrinG extends ExpWithEmbeddedClasses {

	private String _value;

	public StrinG(String str) {
		_value = str;
	}

	public String toString() {
		return _value;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitStrinG(this);
	}

	public Exp split(Exp NIL, StrinG regex) throws GenyrisException {
		Exp result = NIL;
		try {
			String[] splitted = _value.split(regex._value);
			for (int i = splitted.length - 1; i >= 0; i--) {
				result = new Pair(new StrinG(splitted[i]), result);
			}
		} catch (PatternSyntaxException e) {
			throw new GenyrisException(e.getMessage());
		}
		return result;
	}

	public StrinG concat(StrinG str) {
		return new StrinG(this._value.concat(str._value));
	}

	public Exp match(Symbol nil, Symbol true1, StrinG regex) {
		return (_value.matches(regex._value) ? true1 : nil);
	}

	public Exp length() {
		return (new Bignum(_value.length()));
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.STRING();
	}

	public int hashCode() {
		return _value.hashCode();
	}

	public boolean equals(Object compare) {
		if (compare.getClass() != this.getClass())
			return false;
		else
			return _value.equals(((StrinG) compare)._value);
	}

}
