// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;

public interface Environment {

    // TODO reconsider exception - maybe return "unbound" symbol?
    public Exp lookupVariableValue(Exp symbol) throws UnboundException;

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException;

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException;

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException;

    public String toString();

    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException;

    public Lsymbol getNil();

    public Interpreter getInterpreter();

    public Exp internString(String symbolName) throws GenyrisException;

    public Exp internPlainString(String dict);

}