// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;


public class NilSymbol extends Lsymbol {

    public NilSymbol() {
        _printName = "nil";
    }
    public boolean isNil() {
        return true;
    }

    public boolean isMember() {
        return false;
    }

    public String toString() {
        return _printName;
    }

    public Object getJavaValue() {
        return getPrintName();
    }

}
