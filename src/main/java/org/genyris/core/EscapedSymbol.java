// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;


import org.genyris.exception.GenyrisException;

public class EscapedSymbol extends SimpleSymbol {

    public EscapedSymbol(String name) {
        super(name);
    }
	public String toString() {
		return "|" + getPrintName() + "|";
	}
    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitFullyQualifiedSymbol(this);
    }
}
