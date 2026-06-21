package org.genyris.core;

import org.genyris.exception.GenyrisException;

public class URISymbol extends EscapedSymbol {

	public URISymbol(String newSym) {
		super(newSym);
	}
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.URISYMBOL();
	}
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitFullyQualifiedSymbol(this);
	}
}
