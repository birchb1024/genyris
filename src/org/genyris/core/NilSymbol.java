// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;


public class NilSymbol extends SimpleSymbol {

    public NilSymbol() {
        super(Constants.NIL);
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

    public Exp evalSequence(Environment env) throws GenyrisException {
    	return this;
    }
	public int length(Symbol NIL) throws AccessException {
		return 0;
	}
	public Exp nth(int number, Symbol NIL) throws AccessException {
			throw new AccessException("nth called on nil.");
	}

}
