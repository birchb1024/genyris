// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;

import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class MagicEnvironment  extends StandardEnvironment {
    // This environment encompasses an expression (Exp) which provides
    // the first place to look for slots.
    // TODO - rename this class, perhaps.
    private Exp _it;
    public MagicEnvironment(Environment runtime, Exp theObject) {
        super(runtime, new HashMap());
        _it = theObject;
    }

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if(symbol == _classes) {
            return _it.getClasses(_parent);
        }
        else if(symbol == _self) {
            return _it;
        }
        // TODO - DRY
        // TODO - move these into the Lcons class as an Environment
        else if(symbol == _left ) {
            try {
                return _it.car();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if(symbol == _right ) {
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
        while( classes != NIL) {
            try {
                Environment klass = (Environment)(classes.car());
                try {
                    return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
                } catch (UnboundException e) {
                    ;
                } finally {
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
        Lsymbol symbol = (Lsymbol) exp;
        if(symbol == _classes) {
            return _it.getClasses(_parent);
        }
        else if(symbol == _left ) {
            try {
                return _it.car();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if(symbol == _right ) {
            try {
                return _it.cdr();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if(symbol == _self ) {
            return _it;
        }
        else if(symbol.isMember()) {
            return lookupInClasses(symbol);
        }
        else
            return super.lookupVariableValue(symbol);
    }

    public void defineVariable(Exp symbol, Exp valu)  throws GenyrisException
    {
        if(! (symbol instanceof Lsymbol) ) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }

        Lsymbol sym = (Lsymbol) symbol;
        if(symbol == _classes) {
            _it.setClasses(valu, NIL);
            return;
        }
        else if(symbol == _self ) {
            throw new GenyrisException("cannot re-define self.");
        }
        else if( sym.isMember()) {
            throw new GenyrisException("cannot define member slot: " + symbol.toString());
        } else {
            super.defineVariable(symbol, valu);
        }
    }


    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        Lsymbol sym = (Lsymbol) symbol;
        if( sym.isMember()) {
            if(symbol == _classes) {
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
            else if(symbol == _left ) {
                try {
                    _it.setCar(valu);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
            }
            else if(symbol == _right ) {
                try {
                    _it.setCdr(valu);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
            }
        } else {
            super.setVariableValue(symbol, valu);
        }
    }

}
