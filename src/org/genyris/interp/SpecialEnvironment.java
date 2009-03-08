// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.Map;

import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public class SpecialEnvironment extends StandardEnvironment {
    // This environment merges two into one - a dynamic and a lexical.

    private Environment _object;

    public SpecialEnvironment(Environment runtime, Map bindings, Environment object) throws GenyrisException {
        super(runtime, bindings);
        _object = object;
    }

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        if(Symbol.isDynamic(symbol, _dynamic)) {
                _object.defineVariable(symbol, valu);
                return;
        }
        else if (!(symbol instanceof Symbol)) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }
        Symbol sym = (Symbol) symbol;
        if (sym == _self) {
            throw new GenyrisException("cannot re-define !self.");
        } else {
            super.defineVariable(symbol, valu);
        }
    }

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
        if(Symbol.isDynamic(symbol, _dynamic)) {
                return _object.lookupVariableValue(symbol);
            }
        else {
            return super.lookupVariableValue(symbol);
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        Symbol sym = Symbol.realSymbol(symbol, _dynamic);
        if (sym == _self) {
            throw new UnboundException("cannot re-define !self.");
        }
        if(symbol instanceof Symbol)  {
        	super.setVariableValue(symbol, valu);
        }
        else { // object field
            _object.setVariableValue(symbol, valu);
        }
    }

    public String toString() {
        return "<SpecialEnvironment on: " + _object.toString() + ">";
    }

    public Object getJavaValue() {
        return this;
    }
    public Exp lookupDynamicVariableValue(Exp symbol) throws UnboundException {
        return _object.lookupDynamicVariableValue(symbol);
    }
    public Exp getSelf() throws UnboundException {
        return _object.getSelf();
    }

}
