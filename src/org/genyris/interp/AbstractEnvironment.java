// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public abstract class AbstractEnvironment implements Environment {

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
    	
    	if(symbol instanceof Symbol ) {
    		return ((Symbol)symbol).lookupVariableValue(this);
        } else {
            throw new UnboundException("lookupVariableValue needed Symbol: " + symbol);
        }
    }
    

    public Exp lookupDynamicVariableValue(Exp symbol) throws UnboundException {
        throw new UnboundException("no dynamic variables in environment: " + symbol.toString());
    }

    public abstract Exp getSelf() throws UnboundException;

	public Exp lookupDynamicVariableValue(DynamicSymbol symbol) throws UnboundException {
		throw new UnboundException("no dynamic variable in environment: " + symbol);
	}

	public abstract Exp lookupLexicalVariableValue(SimpleSymbol symbol) throws UnboundException ;

	public abstract void defineVariable(Exp symbol, Exp valu) throws GenyrisException;


	public abstract SimpleSymbol getNil();


	public abstract Internable getSymbolTable();


	public abstract Symbol internString(String symbolName);


	public abstract Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException;


	public abstract Exp lookupVariableShallow(Exp symbol) throws UnboundException;


	public abstract void setVariableValue(Exp symbol, Exp valu) throws UnboundException;
}