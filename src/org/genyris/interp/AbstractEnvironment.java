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

	public Exp lookupVariableValue(Symbol symbol) throws UnboundException {
		return symbol.lookupVariableValue(this);
	}

	public abstract Exp getSelf() throws UnboundException;

	public Exp lookupDynamicVariableValue(DynamicSymbol symbol)
			throws UnboundException {
		throw new UnboundException("no dynamic variable in environment: "
				+ symbol);
	}

	public abstract Exp lookupLexicalVariableValue(SimpleSymbol symbol)
			throws UnboundException;

	public void defineVariable(Symbol symbol, Exp valu) throws GenyrisException {
		if (symbol.isNil()) {
			throw new GenyrisException("Attempt to re-define nil!");
		}
		symbol.defineVariable(this, valu);
	}

	public void defineLexicalVariable(SimpleSymbol symbol, Exp valu)
			throws GenyrisException {
		throw new GenyrisException(
				"defineLexicalVariable: no simple variable in environment: "
						+ symbol);
	}

	public void defineDynamicVariable(DynamicSymbol symbol, Exp valu)
			throws GenyrisException {
		throw new GenyrisException(
				"defineDynamicVariable: no dynamic variable in environment: "
						+ symbol);
	}

	public abstract SimpleSymbol getNil();

	public abstract Internable getSymbolTable();

	public abstract Symbol internString(String symbolName);

	public abstract Exp lookupInThisClassAndSuperClasses(DynamicSymbol symbol)
			throws UnboundException;

	public void setVariableValue(Symbol symbol, Exp valu)
			throws UnboundException {
		if (symbol.isNil()) {
			throw new UnboundException("Attempt to re-define nil!");
		}
		symbol.setVariableValue(this, valu);
	}

	public void setLexicalVariableValue(SimpleSymbol symbol, Exp valu)
			throws UnboundException {
		throw new UnboundException(
				"setLexicalVariableValue: no dynamic variable in environment: "
						+ symbol);
	}

	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu)
			throws UnboundException {
		throw new UnboundException(
				"setDynamicVariableValue: no lexical variable in environment: "
						+ symbol);
	}
}