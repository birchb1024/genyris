// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;



public class Linteger extends ExpWithEmbeddedClasses {
    private int _value;

    public Object getJavaValue() {
        return new Integer(_value);
    }

    public Linteger(int i) {
        _value = i;
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitLinteger(this);
    }

    public String toString() {
        return getJavaValue().toString();
    }
    public String getBuiltinClassName() {
        return Constants.INTEGER;
    }

}
