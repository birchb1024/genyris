// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.ExpressionEnvironment;
import org.genyris.interp.UnboundException;

public class TripleEnvironment extends ExpressionEnvironment {
	// This environment encompasses a Pair.

	SimpleSymbol _subject, _predicate, _object;

	public TripleEnvironment(Environment runtime, Triple theObject)
			throws GenyrisException {
		super(runtime, theObject);
		_subject = runtime.getSymbolTable().SUBJECT();
		_predicate = runtime.getSymbolTable().PREDICATE();
		_object = runtime.getSymbolTable().OBJECT();
	}

	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu)
			throws UnboundException {
		throw new UnboundException("Attempt to alter immutable triple: "
				+ _theExpression.toString());
	}

	public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
			throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
		if (sym == _subject) {
			return ((Triple) _theExpression).subject;
		} else if (sym == _predicate) {
			return ((Triple) _theExpression).predicate;
		} else if (sym == _object) {
			return ((Triple) _theExpression).object;
		} else {
			return super.lookupDynamicVariableValue(dsym);
		}

	}

}
