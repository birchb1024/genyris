// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class ExpressionEnvironment extends StandardEnvironment {
    // This environment encompasses an expression (Exp) which provides
    // the first place to look for slots.

	private Exp _theExpression;

    public ExpressionEnvironment(Environment runtime, Exp theObject) throws GenyrisException {
        super(runtime, mapFactory());
        _theExpression = theObject;
    }

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if (symbol == _classes) {
            return _theExpression.getClasses(_parent);
        }
        else if (symbol == _self) {
            return _theExpression;
        }
        // TODO - DRY
        // TODO - move these into the Pair class as an Environment
        else if (symbol == _left) {
            try {
                return _theExpression.car();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if (symbol == _right) {
            try {
                return _theExpression.cdr();
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
        Exp classes = _theExpression.getClasses(_parent);
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

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        Exp sym = Symbol.realSymbol(symbol, _dynamic);

        if (Symbol.isDynamic(symbol, _dynamic)) {
        	if( sym == _classes) {
        		_theExpression.setClasses(valu, NIL);
        	} else if (sym == _self) {
        		throw new GenyrisException("cannot re-define self.");
        	}
        } else {
            super.defineVariable(symbol, valu);
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        Exp sym = Symbol.realSymbol(symbol, _dynamic);

        if (Symbol.isDynamic(symbol, _dynamic)) {
            if (sym == _classes) {
                try {
                    _theExpression.setClasses(valu, NIL);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
                return;
            }
            // TODO - DRY
            // TODO - Move into Pair in an Environment
            else if (sym == _left) {
                try {
                    _theExpression.setCar(valu);
                }
                catch (AccessException e) {
                    throw new UnboundException(e.getMessage());
                }
            }
            else if (sym == _right) {
                try {
                    _theExpression.setCdr(valu);
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
	public Exp lookupDynamicVariableValue(DynamicSymbol dsym) throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
        if (sym == _classes) {
            return _theExpression.getClasses(_parent);
        }
        else if (sym == _left) {
            try {
                return _theExpression.car();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if (sym == _right) {
            try {
                return _theExpression.cdr();
            }
            catch (AccessException e) {
                throw new UnboundException(e.getMessage());
            }
        }
        else if (sym == _self) {
            return _theExpression;
        }
        else {
            return lookupInClasses(sym);
        }

    }
    public Exp getSelf() throws UnboundException {
        return _theExpression;
    }

}
