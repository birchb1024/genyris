package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

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
	public SimpleSymbol getRealSymbol() {
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
