// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.math.BigDecimal;
import java.util.regex.PatternSyntaxException;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class StrinG extends Atom {

	private String _value;
	private char _quoteWith;

	public StrinG(String str) {
		_value = str;
		if (str == null) {
			throw new java.lang.IllegalArgumentException(
					"null passed to String constructor");
		}
		_quoteWith = '\'';
	}

	public StrinG(String str, char quote) {
		_value = str;
		_quoteWith = quote;
	}

	public StrinG(StringBuffer str, char quotechar) {
		_value = str.toString();
		_quoteWith = quotechar;
	}

	public StrinG(char[] array) {
		_value = new String(array);
		_quoteWith = '\'';
	}

	public char getQuoteChar() {
		return _quoteWith;
	}

	public static StrinG makeStringFromInts(Symbol NIL, Exp intList) throws GenyrisException {
		char array[] = new char[intList.length(NIL)];
		int i = 0;
		while( intList != NIL) {
			Exp first = intList.car();
			if( !(first instanceof Bignum) ) {
				throw new GenyrisException("Non-Bignum passed to string constructor: " + first.toString());
			}
			Bignum integer = (Bignum) first;
			array[i] = (char)integer.bigDecimalValue().intValue();
			i += 1;
			intList = intList.cdr();
		}
		return new StrinG(array);
	}
	
	public char getAlternateQuoteChar() {
		return alternateQuoteChar(_quoteWith);
	}

	public static char alternateQuoteChar(char quote) {
		return (quote == '\'' ? '"' : '\'');
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

	public Exp toInts(Exp NIL) throws GenyrisException {
		Exp result = NIL;
		byte[] array = _value.getBytes();
		
		for (int i = array.length - 1; i >= 0; i--) {
			result = new Pair(new Bignum(0x000000FF & ((int)array[i])), result);
		}
		return result;
	}

	public StrinG concat(StrinG str) {
		return new StrinG(this._value.concat(str._value));
	}

	public Exp match(Symbol nil, Symbol true1, StrinG regex) throws GenyrisException {
		try {
		return (_value.matches(regex._value) ? true1 : nil);
		}
		catch(PatternSyntaxException e) {
			throw new GenyrisException(e.getMessage());
		}
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
		if (compare == null)
			return false;
		if (compare.getClass() != this.getClass())
			return false;
		else
			return _value.equals(((StrinG) compare)._value);
	}

	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public Exp replace(StrinG regex, StrinG replacement) {
		return new StrinG(_value.replace(regex.toString(), replacement
				.toString()));
	}

	public Exp slice(BigDecimal start, BigDecimal end) throws GenyrisException {
		try {
			return new StrinG(_value.substring(start.intValue(), end.intValue()+1));
		} catch (StringIndexOutOfBoundsException e) {
			throw new GenyrisException(e.getMessage());
		}
	}

}
