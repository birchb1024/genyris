// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.GenyrisException;



public class SimpleSymbol extends Symbol implements Comparable {

    public SimpleSymbol(String newSym) {
        super(newSym);
    }

	public String getBuiltinClassName() {
	    return Constants.SIMPLESYMBOL;
	}
	
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitSimpleSymbol(this);
	}


}
