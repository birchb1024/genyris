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

public interface Environment {

    public Exp lookupVariableValue(Exp symbol) throws UnboundException;
    public Exp lookupDynamicVariableValue(DynamicSymbol symbol) throws UnboundException;
    public Exp lookupLexicalVariableValue(SimpleSymbol symbol) throws UnboundException;

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException;
    public void defineLexicalVariable(SimpleSymbol symbol, Exp valu) throws GenyrisException;
    public void defineDynamicVariable(DynamicSymbol symbol, Exp valu) throws GenyrisException;

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException;
    public void setLexicalVariableValue(SimpleSymbol symbol, Exp valu) throws UnboundException;
    public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu) throws UnboundException;


    public String toString();

    public Exp lookupInThisClassAndSuperClasses(DynamicSymbol symbol) throws UnboundException;

    public SimpleSymbol getNil();

    public Symbol internString(String symbolName);


    public Exp getSelf() throws UnboundException;

	public Internable getSymbolTable();


}