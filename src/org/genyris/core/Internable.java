package org.genyris.core;

public interface Internable {

	public Symbol internSymbol(Symbol newSym);
	public Symbol internString(String newname);

}