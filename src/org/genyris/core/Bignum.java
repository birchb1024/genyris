// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.math.BigDecimal;

import org.genyris.classes.BuiltinClasses;


public class Bignum extends ExpWithEmbeddedClasses {
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

}
