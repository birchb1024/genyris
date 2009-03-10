package org.genyris.core;

import org.genyris.exception.GenyrisException;

public class FullyQualifiedSymbol extends EscapedSymbol {

	public FullyQualifiedSymbol(String newSym) {
		super(newSym);
	}
	public String getBuiltinClassName() {
	    return Constants.QUALIFIEDSYMBOL;
	}
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitFullyQualifiedSymbol(this);
	}
}
