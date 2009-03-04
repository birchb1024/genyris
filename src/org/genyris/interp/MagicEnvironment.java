// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class MagicEnvironment extends StandardEnvironment {
    // This environment encompasses an expression (Exp) which provides
    // the first place to look for slots.
    // TODO - rename this class, perhaps.
    private Exp _it;

    public MagicEnvironment(Environment runtime, Exp theObject) throws GenyrisException {
        super(runtime, new HashMap());
        _it = theObject;
    }

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if (symbol == _classes) {
            return _it.getClasses(_parent);
        }
        else if (symbol == _self) {
            return _it;
        }
        // TODO - DRY
        // TODO - move these into the Lcons class as an Environment
        else if (symbol == _left) {
            try {
                return _it.car();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if (symbol == _right) {
            try {
                return _it.cdr();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        throw new UnboundException("unbound symbol " + symbol.toString());
    }

    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
        throw new UnboundException("unbound symbol " + symbol.toString());
    }

    private Exp lookupInClasses(Exp symbol) throws UnboundException {
        Exp classes = _it.getClasses(_parent);
        while (classes != NIL) {
            try {
                Environment klass = (Environment) (classes.car());
                try {
                    return (Exp) klass.lookupInThisClassAndSuperClasses(symbol);
                }
                catch (UnboundException e) {
                    ;
                }
                finally {
                    classes = classes.cdr();
                }
            }
            catch (AccessException e) {
                throw new UnboundException("bad classes list in object");
            }
        }
        throw new UnboundException("unbound symbol: " + symbol.toString());
    }

    public Exp lookupVariableValue(Exp exp) throws UnboundException {
        Exp sym = exp;
        boolean isMember = false;
        if (exp.listp()) {
            Lcons tmp = (Lcons) exp;
            if (tmp.car() == _dynamic) {
                tmp = (Lcons) tmp.cdr(); // TODO unsafe downcast
                sym = tmp.car();
                isMember = true;
            }
            else {
                throw new UnboundException("bad dynamic: " + exp.toString());
            }
        }
        SimpleSymbol symbol = (SimpleSymbol) sym;
        if (isMember) {
            return lookupDynamicVariableValue(sym);
        }
        else
            return super.lookupVariableValue(symbol);
    }

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        Exp sym = symbol;
        boolean isMember = false;
        if (symbol.listp()) {
            Lcons tmp = (Lcons) symbol;
            if (tmp.car() == _dynamic) {
                tmp = (Lcons) tmp.cdr(); // TODO unsafe downcast
                sym = tmp.car();
                isMember = true;
            }
            else {
                throw new UnboundException("cannot set to a bad place" + symbol.toString());
            }
        }
        if (!(sym instanceof Symbol)) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }

        if (isMember && sym == _classes) {
            _it.setClasses(valu, NIL);
            return;
        }
        else if (isMember && sym == _self) {
            throw new GenyrisException("cannot re-define self.");
        }
        else {
            super.defineVariable(symbol, valu);
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        boolean isMember = false;
        Exp sym = symbol;
        if (symbol.listp()) {
            Lcons tmp = (Lcons) symbol;
            if (tmp.car() == _dynamic) {
                tmp = (Lcons) tmp.cdr(); // TODO unsafe downcast
                sym = tmp.car();
                isMember = true;
            }
            else {
                throw new UnboundException("cannot set to a bad place" + symbol.toString());
            }
        }
        if (isMember) {
            if (sym == _classes) {
                try {
                    _it.setClasses(valu, NIL);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
                return;
            }
            // TODO - DRY
            // TODO - Move into Lcons in an Environment
            else if (sym == _left) {
                try {
                    _it.setCar(valu);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
            }
            else if (sym == _right) {
                try {
                    _it.setCdr(valu);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
            }
        }
        else {
            super.setVariableValue(symbol, valu);
        }
    }

    public Exp lookupDynamicVariableValue(Exp sym) throws UnboundException {

        if (sym == _classes) {
            return _it.getClasses(_parent);
        }
        else if (sym == _left) {
            try {
                return _it.car();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if (sym == _right) {
            try {
                return _it.cdr();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if (sym == _self) {
            return _it;
        }
        else {
            return lookupInClasses(sym);
        }

    }
    public Exp getSelf() throws UnboundException {
        return _it;
    }

}
