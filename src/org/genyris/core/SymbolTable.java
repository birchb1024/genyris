// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.genyris.exception.GenyrisException;

public class SymbolTable implements Internable {
    private Map         _table;
    private SimpleSymbol     NIL;

    public SymbolTable() {
        _table = new TreeMap();
    }

    public void init(SimpleSymbol nil) {
        NIL = nil;
        _table.put("nil", NIL);
        bindKeyword(Constants.SELF);
        bindKeyword(Constants.CLASSES);
        bindKeyword(Constants.SUPERCLASSES);
        bindKeyword(Constants.CLASSNAME);
        bindKeyword(Constants.VARS);
        bindKeyword(Constants.DYNAMIC_SYMBOL);

    }

	private void bindKeyword(String name) {
		_table.put(name, new SimpleSymbol(name));
	}

    public SimpleSymbol lookupString(String newSym) throws GenyrisException {
        if (_table.containsKey(newSym)) {
            return (SimpleSymbol)_table.get(newSym);
        } else {
            throw new GenyrisException("symbol not found in symbol table " + newSym);
        }
    }

    public Symbol internString(String newSym) {
        if (_table.containsKey(newSym)) {
            return (Symbol)_table.get(newSym);
        } else {
            SimpleSymbol sym = new SimpleSymbol(newSym);
            _table.put(newSym, sym);
            return sym;
        }
    }

    public Symbol internSymbol(Symbol newSym) {
        if (_table.containsKey(newSym.getPrintName())) {
            return (Symbol)_table.get(newSym.getPrintName());
        } else {
            _table.put(newSym.getPrintName(), newSym);
            return newSym;
        }
    }

    public Exp getNil() {
        return NIL;
    }

    public Exp getSymbolsList() {
        Lcons head, tail;
        Iterator iter = _table.values().iterator();
        Exp key = (Exp)iter.next(); // Safe to assume symbol table is never empty
        head = tail = new Lcons(key,NIL);
        while(iter.hasNext()) {
            key = (Exp)iter.next();
            Lcons newItem = new Lcons(key,NIL);
            tail.setCdr(newItem);
            tail = newItem;
        }
        return head;
    }
}
