// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.GenyrisException;


public class Ldouble extends ExpWithEmbeddedClasses {

    private double _value;

    public Ldouble(double d) {
        _value = d;}

    public Object getJavaValue() { return new Double(_value); }

    public void acceptVisitor(Visitor guest)  throws GenyrisException {
        guest.visitLdouble(this);
    }

    public String toString() {
        return getJavaValue().toString();
    }

    public String getBuiltinClassName() {
        return Constants.DOUBLE;
    }

}
