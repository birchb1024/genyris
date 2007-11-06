// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;

import org.genyris.core.AccessException;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;

public class MagicEnvironment  extends StandardEnvironment {
    // This environment encompasses an expression (Exp) which provides
    // the first place to look for slots.
    private Exp _it;
    public MagicEnvironment(Environment runtime, Exp theObject) {
        super(runtime, new HashMap());
        _it = theObject;
    }

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if(symbol == _classes) {
            return _it.getClasses(NIL);
        }
        else if(symbol == _self) {
            return _it;
        }
        throw new UnboundException("unbound symbol " + symbol.toString());
    }

    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
        throw new UnboundException("unbound symbol " + symbol.toString());
    }

    private Exp lookupInClasses(Exp symbol) throws UnboundException {
        Exp classes = _it.getClasses(NIL);
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
            return _it.getClasses(NIL);
        }
        else if(symbol == _self || symbol == __self) {
            return _it;
        }
        if(symbol.isMember()) {
            return lookupInClasses(symbol);
        }
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
        if( sym.isMember()) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }
        } else {
            super.setVariableValue(symbol, valu);
        }
    }

}
