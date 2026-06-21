// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import com.google.common.graph.Graph;
import org.genyris.core.*;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.ExpressionEnvironment;
import org.genyris.interp.UnboundException;

public class GraphEnvironment extends ExpressionEnvironment {
	// This environment encompasses a Graph
	SimpleSymbol _subjects, _length;

	public GraphEnvironment(Environment env, AbstractGraph theObject)
			throws GenyrisException {
		super(env, (Exp)theObject);
		_subjects = env.getSymbolTable().SUBJECTS();
		_length = env.getSymbolTable().LENGTH();
	}

//	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu)
//			throws UnboundException {
//		throw new UnboundException("Attempt to alter immutable triple: "
//				+ _theExpression.toString());
//	}

	public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
			throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
		if (sym == _subjects) {
                return ((AbstractGraph)_theExpression).subjects(NIL);
        } else if (sym == _length) {
			return new Bignum(((AbstractGraph)_theExpression).length());
		} else {
			return super.lookupDynamicVariableValue(dsym);
		}

	}

}
