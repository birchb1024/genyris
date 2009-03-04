// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.Map;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.SimpleSymbol;
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
        if(symbol.listp()) {
            if(symbol.car() == _dynamic) {
                _object.defineVariable(symbol, valu);
                return;
            }
        }
        else if (!(symbol instanceof SimpleSymbol)) {
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
        if(symbol.listp()) {
            Lcons tmp = (Lcons)symbol;
            if(tmp.car() == _dynamic) {
                tmp = (Lcons)tmp.cdr(); // TODO unsafe downcast
                return _object.lookupVariableValue(tmp.car());
            } else {
                throw new UnboundException("cannot set to a bad place" + symbol.toString());
            }
        }
        else {
            return super.lookupVariableValue(symbol);
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        boolean isMember = false;
        Exp symbolatom = null;
        if(symbol.listp()) {
            Lcons tmp = (Lcons)symbol;
            if(tmp.car() == _dynamic) {
                tmp = (Lcons)tmp.cdr(); // TODO unsafe downcast
                symbolatom = tmp.car();
                isMember = true;
            } else {
                throw new UnboundException("cannot set to a bad place" + symbol.toString());
            }
        }
        Symbol sym = (Symbol) symbolatom;
        if (sym == _self) {
            throw new UnboundException("cannot re-define !self.");
        }
        else if (isMember) {
            _object.setVariableValue(symbol, valu);
        }
        else {
            super.setVariableValue(symbol, valu);
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
