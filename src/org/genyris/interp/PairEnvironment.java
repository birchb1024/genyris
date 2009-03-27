// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public class PairEnvironment extends ExpressionEnvironment {
	// This environment encompasses a Pair.

	public PairEnvironment(Environment runtime, Pair theObject)
			throws GenyrisException {
		super(runtime, theObject);
	}

	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu) throws UnboundException {
		Exp sym = symbol.getRealSymbol();
		
		if (sym == _left) {
			((Pair)_theExpression).setCar(valu);
		} else if (sym == _right) {
			((Pair)_theExpression).setCdr(valu);
		} else {
			super.setDynamicVariableValue(symbol, valu);
		}
	}

	public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
			throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
		if (sym == _left) {
			return ((Pair)_theExpression).car();
		} else if (sym == _right) {
			return ((Pair)_theExpression).cdr();
		} else {
			return super.lookupDynamicVariableValue(dsym);
		}

	}

}
