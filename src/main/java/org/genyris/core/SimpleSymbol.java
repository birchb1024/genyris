// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;



public class SimpleSymbol extends Symbol {

    protected String _printName;

    public SimpleSymbol(int newSym) {
        _printName = String.valueOf(newSym);
    }
    public SimpleSymbol(String newSym) {
        _printName = newSym;
    }
    @Override
    public String getPrintName() {
        return _printName;
    }

    @Override
    public int hashCode() {
        return _printName.hashCode();
    }

    @Override
	public boolean equals(Object other) {
        if (other == null || !(other instanceof SimpleSymbol)) {
            return false;
        }
        return _printName.equals(((SimpleSymbol)other)._printName) ;
    }

    @Override
    public int compareTo(Object arg0) {
		if(!(arg0 instanceof SimpleSymbol)) {
			return -1;
		}
    	return this.getPrintName().compareTo(((SimpleSymbol) arg0).getPrintName());
    }
	
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitSimpleSymbol(this);
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.SIMPLESYMBOL();
	}
	public void defineVariable(Environment env, Exp valu) throws GenyrisException {
		env.defineLexicalVariable(this, valu);
	}
	public Exp lookupVariableValue(Environment env) throws UnboundException {
		return env.lookupLexicalVariableValue(this);
	}
	public void setVariableValue(Environment env, Exp valu) throws UnboundException {
		env.setLexicalVariableValue(this, valu);		
	}


}
