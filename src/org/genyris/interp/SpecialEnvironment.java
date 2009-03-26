// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.Map;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;

public class SpecialEnvironment extends StandardEnvironment {
    // This environment merges two into one - a dynamic and a lexical.

    private Environment _object;

    public SpecialEnvironment(Environment runtime, Map bindings, Environment object) throws GenyrisException {
        super(runtime, bindings);
        _object = object;
    }
    
    public void defineDynamicVariable(DynamicSymbol symbol, Exp valu) throws GenyrisException {
        _object.defineDynamicVariable(symbol , valu);
    }

    public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu) throws UnboundException {
        _object.setDynamicVariableValue(symbol, valu);
    }

    public String toString() {
        return "<SpecialEnvironment on: " + _object.toString() + ">";
    }

    public Exp lookupDynamicVariableValue(DynamicSymbol symbol) throws UnboundException {
        return _object.lookupDynamicVariableValue(symbol);
    }

    public Exp getSelf() throws UnboundException {
        return _object.getSelf();
    }

}
