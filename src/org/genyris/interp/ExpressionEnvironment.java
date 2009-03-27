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

	protected Exp _theExpression;

	public ExpressionEnvironment(Environment runtime, Exp theObject)
			throws GenyrisException {
		super(runtime, mapFactory());
		_theExpression = theObject;
	}

	public Exp lookupInThisClassAndSuperClasses(DynamicSymbol symbol)
			throws UnboundException {
		throw new UnboundException("unbound symbol " + symbol.toString());
	}

	protected Exp lookupInClasses(DynamicSymbol symbol) throws UnboundException {
		Exp classes = _theExpression.getClasses(_parent);
		while (classes != NIL) {
			try {
				Environment klass = (Environment) (classes.car());
				try {
					return (Exp) klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					classes = classes.cdr();
				}
			} catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("unbound symbol: " + symbol.toString());
	}

	public void defineDynamicVariable(DynamicSymbol sym, Exp valu)
			throws GenyrisException {
		if (sym.getRealSymbol() == _classes) {
			_theExpression.setClasses(valu, NIL);
		} else if (sym.getRealSymbol() == _self) {
			throw new GenyrisException("cannot re-define self.");
		}
	}

	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu)
			throws  UnboundException {
		Exp sym = symbol.getRealSymbol();

		if (sym == _classes) {
			try {
				_theExpression.setClasses(valu, NIL);
			} catch (AccessException e) {
				throw new UnboundException(e.getMessage());
			}
			return;
		}
	}

	public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
			throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
		if (sym == _classes) {
			return _theExpression.getClasses(_parent);
		} 
		else if (sym == _self) {
			return _theExpression;
		} else {
			return lookupInClasses(dsym);
		}

	}

	public Exp getSelf() throws UnboundException {
		return _theExpression;
	}

}
