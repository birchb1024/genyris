// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;



public class SimpleSymbol extends Symbol implements Comparable {

    protected String _printName;

    public SimpleSymbol(int newSym) {
        _printName = String.valueOf(newSym);
    }
    public SimpleSymbol(String newSym) {
        _printName = newSym;
    }
    public String getPrintName() {
        return _printName;
    }
    public boolean equals(Object arg) {
    	// keep FindBugs happy.
    	return super.equals(arg);
    }
    public int hashCode() {
        return super.hashCode();
    }
    public int compareTo(Object arg0) {
    	if( equals(arg0) )
    		return 0;
    	return this._printName.compareTo(((SimpleSymbol) arg0)._printName);
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
