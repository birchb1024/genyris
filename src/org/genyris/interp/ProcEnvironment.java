// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class ProcEnvironment extends ExpressionEnvironment {
	// This environment encompasses an applicable function or closure.
	private Symbol SOURCE;
	private Symbol NAME;
	public ProcEnvironment(Environment runtime, AbstractClosure abstractClosure)
			throws GenyrisException {
		super(runtime, abstractClosure);
		SOURCE = _parent.getSymbolTable().SOURCE();
		NAME = _parent.getSymbolTable().NAME();
	}

	public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
			throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
		if (sym == SOURCE) {
			try {
				return ((AbstractClosure)_theExpression).getCode();
			} catch (AccessException e) {
				throw new UnboundException(e.getMessage());
			}
		} else if (sym == NAME) {
			return new StrinG(((AbstractClosure)_theExpression).getName());
		} else {
			return super.lookupDynamicVariableValue(dsym);
		}

	}

}
