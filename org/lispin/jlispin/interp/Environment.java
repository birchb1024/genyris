package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public abstract class Environment extends Exp {

	public abstract Exp lookupVariableValue(Exp symbol) throws UnboundException;

	public abstract void setVariableValue(Exp symbol, Exp valu)
			throws UnboundException;

	public abstract void defineVariable(Exp symbol, Exp valu);

	public abstract String toString();

}