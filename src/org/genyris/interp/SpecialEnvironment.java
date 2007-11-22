// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.Map;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;

public class SpecialEnvironment extends StandardEnvironment {
    // This environment merges two into one - a dynamic and a lexical.

    private Environment _object;

    public SpecialEnvironment(Environment runtime, Map bindings, Environment object) {
        super(runtime, bindings);
        _object = object;
    }

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        if (!(symbol instanceof Lsymbol)) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }
        Lsymbol sym = (Lsymbol) symbol;
        if (sym == _self) {
            throw new GenyrisException("cannot re-define self.");
        }
        else if (sym.isMember()) {
            _object.defineVariable(symbol, valu);
        }
        else {
            super.defineVariable(symbol, valu);
        }
    }

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
        Lsymbol sym = (Lsymbol) symbol;
        if (sym == _self || sym.isMember()) {
            return _object.lookupVariableValue(symbol);
        }
        else {
            return super.lookupVariableValue(symbol);
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        Lsymbol sym = (Lsymbol) symbol;
        if (sym == _self) {
            throw new UnboundException("cannot re-define self.");
        }
        else if (sym.isMember()) {
            _object.setVariableValue(symbol, valu);
        }
        else {
            super.setVariableValue(symbol, valu);
        }
    }

    public String toString() {
        return "<SpecialEnvironment>";
    }

    public Object getJavaValue() {
        return this;
    }

}
