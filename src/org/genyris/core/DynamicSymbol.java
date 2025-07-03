package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class DynamicSymbol extends Symbol {

	private Symbol _realSymbol;
	
	public DynamicSymbol(Symbol sym) {
		_realSymbol = sym;
	}
    public int compareTo(Object arg0) {
		return ((DynamicSymbol) arg0)._realSymbol.compareTo(_realSymbol);
    }

    public boolean equals(Object arg0) {
    	if(!(arg0 instanceof DynamicSymbol)) {
    		return false;
    	}
    	return _realSymbol.equals(((DynamicSymbol)arg0)._realSymbol);
    }

    public int hashCode() {
        return _realSymbol.hashCode();
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitDynamicSymbol(this);
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.DYNAMICSYMBOLREF();
	}
    public String toString() {
        return "."+ _realSymbol.toString();
    }

	public String getPrintName() {
		return "."+ _realSymbol.getPrintName();
	}
	public Symbol getRealSymbol() {
		return _realSymbol;
	}
	public void defineVariable(Environment env, Exp valu) throws GenyrisException {
		env.defineDynamicVariable(this, valu);
	}
	public Exp lookupVariableValue(Environment env) throws UnboundException {
		return env.lookupDynamicVariableValue(this);
	}
	public void setVariableValue(Environment env, Exp valu) throws UnboundException {
		env.setDynamicVariableValue(this, valu);
	}

}
