// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

// TODO Break this into a Root environment and a Standard Env....
public class StandardEnvironment extends AbstractEnvironment {

	Map _frame; // Exp, Exp

	protected Environment _parent;

	protected SimpleSymbol NIL;

	protected Symbol _self, _classes, _superclasses, _classname, _vars;

	protected Symbol _left, _right, _dynamic, _lineNumber, _filename;

	private Internable _table;

	protected static Map mapFactory() {
		return new HashMap();
	}

	public StandardEnvironment(Internable table, NilSymbol nil) {
		_parent = null;
		_frame = mapFactory();
		NIL = nil;
		_table = table;

		initConstants(table);
	}

	private void initConstants(Internable table) {
		if (table == null)
			return;
		_self = table.SELF();
		_vars = table.VARS();
		_classes = table.CLASSES();
		_superclasses = table.SUPERCLASSES();
		_classname = table.CLASSNAME();
		_left = table.LEFT();
		_right = table.RIGHT();
		_dynamic = table.DYNAMIC_SYMBOL();
		_lineNumber = table.LINENUMBER();
		_filename = table.FILENAME();
	}

	private void init() {
		NIL = _parent.getNil();
		Internable table = _parent.getSymbolTable();
		initConstants(table);
	}

	public StandardEnvironment(Environment parent) {
		_parent = parent;
		_frame = mapFactory();
		init();
	}

	public StandardEnvironment(Environment parent, Map bindings) {
		_parent = parent;
		_frame = bindings;
		init();
	}

	public void setLexicalVariableValue(SimpleSymbol symbol, Exp valu)
			throws UnboundException {
		if (_frame.containsKey(symbol)) {
			_frame.put(symbol, valu);
		} else if (_parent == null) {
			throw new UnboundException("unbound: " + symbol.toString());
		} else {
			_parent.setLexicalVariableValue(symbol, valu);
		}
	}

	public void defineLexicalVariable(SimpleSymbol symbol, Exp valu)
			throws GenyrisException {
		_frame.put(symbol, valu);
	}

	public String toString() {
		return (_parent != null ? _parent.toString() : "/") + _frame.toString();
	}

	public Exp lookupInThisClassAndSuperClasses(DynamicSymbol symbol)
			throws UnboundException {
		throw new UnboundException(
				"lookupInSuperClasses not implemented for standard environments.");
	}

	public SimpleSymbol getNil() {
		return NIL;
	}

	public Symbol internString(String symbolName) {
		if (_table == null) {
			return _parent.internString(symbolName);
		} else {
			return _table.internString(symbolName);
		}
	}

	public Exp getSelf() throws UnboundException {
		throw new UnboundException(
				"no dynamic variable self in standard environments.");
	}

	public Internable getSymbolTable() {
		if (_table != null) {
			return _table;
		} else {
			return _parent.getSymbolTable();
		}
	}


	public Exp lookupLexicalVariableValue(SimpleSymbol symbol)
			throws UnboundException {
		Object result = _frame.get(symbol);
		if(result != null ) {
			return (Exp) result;
		} else if (_parent == null) {
			throw new UnboundException("unbound variable: " + symbol.toString());
		} else {
			return _parent.lookupVariableValue(symbol);
		}
	}

    public boolean isBound(Symbol symbol) {
        // TOD DRY
        Object result = _frame.get(symbol);
        if(result != null ) {
            return true;
        } else if (_parent == null) {
            return false;
        } else {
            return _parent.isBound(symbol);
        }
    }

}
