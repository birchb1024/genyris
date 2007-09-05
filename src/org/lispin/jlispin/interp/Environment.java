package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;

public interface Environment {

	// TODO reconsider exception - maybe return "unbound" symbol?
	public Exp lookupVariableValue(Exp symbol) throws UnboundException;

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException;

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException;

	public void defineVariable(Exp symbol, Exp valu);

	public String toString();

	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException;

	public Lsymbol getNil();

	public Interpreter getInterpreter();

	public Exp internString(String symbolName);
}