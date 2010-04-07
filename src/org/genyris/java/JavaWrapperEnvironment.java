// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.java;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.ExpressionEnvironment;
import org.genyris.interp.UnboundException;

public class JavaWrapperEnvironment extends ExpressionEnvironment {

	private Symbol _javaclass;

	public JavaWrapperEnvironment(Environment runtime, JavaWrapper theObject)
			throws GenyrisException {
		super(runtime, theObject);
		_javaclass = runtime.getSymbolTable().JAVACLASS();
	}

	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu) throws UnboundException {
		Exp sym = symbol.getRealSymbol();
		JavaWrapper it = (JavaWrapper)_theExpression;
		try {
			it.setField(sym.toString(), valu, this);
		} catch (GenyrisException e) {
			throw new UnboundException(e.getMessage());
		}
	}

	public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
			throws UnboundException {
		Symbol sym = dsym.getRealSymbol();
		JavaWrapper it = (JavaWrapper)_theExpression;
		if (sym == _vars) {
			return it.dir(getSymbolTable());
		} else if (sym == _javaclass) {
			return new StrinG(it.getValue().getClass().getName());
		} else if( it.hasField(sym) ){
			return it.getField(this, sym);
		} else {
			return super.lookupDynamicVariableValue(dsym);
		}

	}

}
