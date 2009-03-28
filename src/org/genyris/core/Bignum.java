// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.math.BigDecimal;
import java.math.MathContext;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class Bignum extends Atom {
	private BigDecimal _value;

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.BIGNUM();
	}

	public Bignum(BigDecimal i) {
		_value = i;
	}

	public Bignum(int i) {
		_value = new BigDecimal(i);
	}

	public Bignum(double d) {
		_value = new BigDecimal(d);
	}

	public Bignum(String string) {
		_value = new BigDecimal(string);
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitBignum(this);
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
			return _value.equals(((Bignum)compare)._value);
	}
    public Bignum divide(Bignum other) {
    	return new Bignum(_value.divide(other._value, 150, BigDecimal.ROUND_HALF_UP));
    }
    public boolean lessThan(Bignum other) {
		return(_value.compareTo(other._value) < 0) ? true : false;
    }
    public boolean greaterThan(Bignum other) {
		return(_value.compareTo(other._value) > 0) ? true : false;
    }

	public Bignum subtract(Bignum other) {
		return(new Bignum(_value.subtract(other._value)));
	}

	public Exp negate() {
		return(new Bignum(_value.negate()));
	}

	public Exp multiply(Bignum other) {
		return(new Bignum(_value.multiply(other._value)));
	}

	public Exp add(Bignum other) {
		return(new Bignum(_value.add(other._value)));
	}

	public Exp pow(Bignum other) {
        return new Bignum(_value.pow(other._value.intValueExact(), new MathContext(100000)));
	}

	public Exp remainder(Bignum other) {
		return(new Bignum(_value.remainder(other._value)));
	}

	public double doubleValue() {
		return _value.doubleValue();
	}
	public BigDecimal bigDecimalValue() {
		return _value;
	}

	public Exp eval(Environment env) {
		return this;
	}
}
