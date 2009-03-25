package org.genyris.core;

import org.genyris.exception.GenyrisException;

public class DynamicSymbol extends Symbol {

	private SimpleSymbol _realSymbol;
	
	public DynamicSymbol(SimpleSymbol sym) {
		_realSymbol = sym;
	}
    public int compareTo(Object arg0) {
        return ((DynamicSymbol) arg0)._realSymbol.compareTo(_realSymbol);
    }
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitDynamicSymbol(this);
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.DYNAMICSYMBOLREF();
	}
	public String getPrintName() {
		return "!" + _realSymbol.getPrintName();
	}
	public Symbol getRealSymbol() {
		return _realSymbol;
	}

}
