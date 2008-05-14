// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.interp.Environment;


//public class Lsymbol extends ExpWithEmbeddedClasses implements Comparable {
public class Lsymbol extends Lobject implements Comparable {

    protected String _printName;

    public Lsymbol(String newSym) {
        _printName = newSym;
    }

    public void init(Environment env) {
        _parent = env;
        super.init(env);
    }
    public boolean isNil() {
        return false;
    }

    public int hashCode() {
        return getPrintName().hashCode();
    }

    public boolean equals(Object compare) {
        return this == compare;
    }
    public String getPrintName() {
        return _printName;
    }
    public Object getJavaValue() {
        return getPrintName();
    }

    public boolean isSelfEvaluating() {
        return false;
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitLsymbol(this);
    }

    public String toString() {
        return getPrintName();
    }
    public String getBuiltinClassName() {
        return Constants.SYMBOL;
    }

    public int compareTo(Object arg0) {
        if(arg0 instanceof Lsymbol) {
            return ((Lsymbol) arg0)._printName.compareTo(this._printName);
//            return this._printName.compareTo(((Lsymbol) arg0)._printName);
        } else {
            throw new ClassCastException("Lsymbol compareTo on " + arg0);
        }
    }

    public void setParent(Environment env) {
        _parent = env;
    }

}
