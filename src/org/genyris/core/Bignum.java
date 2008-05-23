// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.math.BigDecimal;


public class Bignum extends ExpWithEmbeddedClasses implements Comparable {
    private BigDecimal _value;

    public Object getJavaValue() {
        return _value;
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

    public void acceptVisitor(Visitor guest) {
        guest.visitBignum(this);
    }

    public String toString() {
        return _value.toString();
    }

    public String getBuiltinClassName() {
        return Constants.BIGNUM;
    }

    public int compareTo(Object arg0) {
        if(arg0 instanceof Bignum) {
            return ((Bignum) arg0)._value.compareTo(this._value);
        } else {
            return -999999;
        }
    }


}
