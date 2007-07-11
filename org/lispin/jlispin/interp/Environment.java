package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public interface Environment {

	// TODO reconsider exception - maybe return "unbound" symbol?
	public abstract Exp lookupVariableValue(Exp symbol) throws UnboundException;

	public abstract Exp lookupVariableShallow(Exp symbol) throws UnboundException;

	public abstract void setVariableValue(Exp symbol, Exp valu) throws UnboundException;

	public abstract void defineVariable(Exp symbol, Exp valu);

	public abstract String toString();

	public abstract Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException;

}