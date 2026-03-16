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

    Exp lookupVariableValue(Symbol symbol) throws UnboundException;
    Exp lookupDynamicVariableValue(DynamicSymbol symbol) throws UnboundException;
    Exp lookupLexicalVariableValue(SimpleSymbol symbol) throws UnboundException;

    void defineVariable(Symbol symbol, Exp valu) throws GenyrisException;
    void defineLexicalVariable(SimpleSymbol symbol, Exp valu) throws GenyrisException;
    void defineDynamicVariable(DynamicSymbol symbol, Exp valu) throws GenyrisException;

    void setVariableValue(Symbol symbol, Exp valu) throws UnboundException;
    void setLexicalVariableValue(SimpleSymbol symbol, Exp valu) throws UnboundException;
    void setDynamicVariableValue(DynamicSymbol symbol, Exp valu) throws UnboundException;


    String toString();

    Exp lookupInThisClassAndSuperClasses(DynamicSymbol symbol) throws UnboundException;

    SimpleSymbol getNil();

    Symbol internString(String symbolName);

    Exp getSelf() throws UnboundException;

	Internable getSymbolTable();
    boolean isBound(Symbol s);


}