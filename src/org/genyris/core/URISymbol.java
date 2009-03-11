package org.genyris.core;

import org.genyris.exception.GenyrisException;

public class URISymbol extends EscapedSymbol {

	public URISymbol(String newSym) {
		super(newSym);
	}
	public String getBuiltinClassName() {
	    return Constants.URISYMBOL;
	}
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitFullyQualifiedSymbol(this);
	}
}
