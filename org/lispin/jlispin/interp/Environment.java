package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public abstract class Environment extends Exp {

	// TODO reconsider exception - maybe return "unbound" symbol?
	public abstract Exp lookupVariableValue(Exp symbol) throws UnboundException; 
	
	public abstract Exp lookupVariableShallow(Exp symbol) throws UnboundException; 

	public boolean boundp(Exp symbol) {
		try {
			lookupVariableValue(symbol);
			return true;
		}
		catch (UnboundException e) {
			return false;
		}
	}
	
	public abstract void setVariableValue(Exp symbol, Exp valu)
			throws UnboundException;

	public abstract void defineVariable(Exp symbol, Exp valu);

	public abstract String toString();

}