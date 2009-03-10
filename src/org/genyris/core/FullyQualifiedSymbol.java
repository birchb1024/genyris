package org.genyris.core;

public class FullyQualifiedSymbol extends Symbol {

	public FullyQualifiedSymbol(String newSym) {
		super(newSym);
	}
	public String getBuiltinClassName() {
	    return Constants.QUALIFIEDSYMBOL;
	}

}