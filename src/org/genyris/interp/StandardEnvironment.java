// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

// TODO Break this into a Root environment and a Standard Env....
public class StandardEnvironment implements Environment {

    Map _frame; // Exp, Exp
    Environment _parent;
    protected SimpleSymbol NIL;
    protected Symbol _self, _classes, _superclasses, _classname;
    protected Symbol _left, _right, _dynamic;
    private Internable _table;


    public StandardEnvironment(NilSymbol nil) {
        _parent = null;
        _frame = new HashMap();
        NIL = nil;
        _table = null;
    }

    public StandardEnvironment(Internable table, NilSymbol nil) {
        _parent = null;
        _frame = new HashMap();
        NIL = nil;
        _table = table;

      initConstants(table);
    }

	private void initConstants(Internable table) {
		_self = table.SELF();
		  _classes = table.CLASSES();
		  _superclasses = table.SUPERCLASSES();
		  _classname = table.CLASSNAME();
		  _left = table.LEFT();
		  _right = table.RIGHT();
		  _dynamic = table.DYNAMIC_SYMBOL();
	}

    private void init() {
        NIL = _parent.getNil();
        Internable table = _parent.getSymbolTable();
        initConstants(table);
    }
    public StandardEnvironment(Environment parent) {
        _parent = parent;
        _frame = new HashMap();
        init();
    }
    public StandardEnvironment(Environment parent, Map bindings) {
        _parent = parent;
        _frame = bindings;
        init();
    }

    public Object getJavaValue() {
        return "<StandardEnvironment>";
    }

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
        if( _frame.containsKey(symbol) ) {
            return (Exp)_frame.get(symbol);
        }
        else if(_parent == null) {
            throw new UnboundException("unbound variable: " + symbol.toString());
        }
        else {
            return _parent.lookupVariableValue(symbol);
        }
    }
    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if( _frame.containsKey(symbol) ) {
            return (Exp)_frame.get(symbol);
        } else {
            throw new UnboundException("frame does not contain key: " + symbol.toString());
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        if(! (symbol instanceof Symbol) ) {
            throw new UnboundException("cannot set non-symbol: " + symbol.toString());
        }
        if( _frame.containsKey(symbol) ) {
            _frame.put(symbol, valu);
        }
        else if(_parent == null) {
            throw new UnboundException("unbound: " + symbol.toString());
        }
        else {
            _parent.setVariableValue(symbol, valu);
        }
    }

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        if(! (symbol instanceof Symbol) ) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }
        _frame.put(symbol, valu);
    }



    public String toString() {
        if( _parent != null ) {
            return _parent.toString() + "/" + _frame.toString();
        }
        else {
            return "/" + _frame.toString();
        }
    }
    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
        throw new UnboundException("lookupInSuperClasses not implemented for standard environments.");
    }

    public SimpleSymbol getNil() {
        return NIL;
    }

    public Symbol internString(String symbolName) {
        if(_table == null) {
            return _parent.internString(symbolName);
        }
        else {
            return _table.internString(symbolName);
        }
    }

    public Exp lookupDynamicVariableValue(Exp symbol) throws UnboundException {
        throw new UnboundException("no dynamic variables in standard environments: " + symbol.toString());
    }

    public Exp getSelf() throws UnboundException {
        throw new UnboundException("no dynamic variable self in standard environments.");
    }

	public Exp getCLASSES() {
		return _classes;
	}

	public Internable getSymbolTable() {
		if(_table != null) {
			return _table;
		}
		else {return _parent.getSymbolTable();}
	}

}
