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

public class SymbolTable {
    private Map         _table;
    private SimpleSymbol     NIL;

    public SymbolTable() {
        _table = new TreeMap();
    }

    public void init(SimpleSymbol nil) throws GenyrisException {
        NIL = nil;
        _table.put("nil", NIL);
        _table.put(Constants.SELF, new SimpleSymbol(Constants.SELF));
        _table.put(Constants.CLASSES, new SimpleSymbol(Constants.CLASSES));
        _table.put(Constants.SUPERCLASSES, new SimpleSymbol(Constants.SUPERCLASSES));
        _table.put(Constants.CLASSNAME, new SimpleSymbol(Constants.CLASSNAME));
        _table.put(Constants.VARS, new SimpleSymbol(Constants.VARS));
        _table.put(Constants.DYNAMIC_SYMBOL, new SimpleSymbol(Constants.DYNAMIC_SYMBOL));

    }

    public SimpleSymbol lookupString(String newSym) throws GenyrisException {
        if (_table.containsKey(newSym)) {
            return (SimpleSymbol)_table.get(newSym);
        } else {
            throw new GenyrisException("symbol not found in symbol table " + newSym);
        }
    }

    public SimpleSymbol internString(String news) throws GenyrisException {
        return internPlainString(news);
    }

    public SimpleSymbol internPlainString(String newSym) {
        if (_table.containsKey(newSym)) {
            return (SimpleSymbol)_table.get(newSym);
        } else {
            SimpleSymbol sym = new SimpleSymbol(newSym);
            _table.put(newSym, sym);
            return sym;
        }
    }

    public void intern(Exp newSym) throws Exception {
        if (_table.containsKey(((SimpleSymbol)newSym).getPrintName())) {
            throw new GenyrisException("Can't intern symbol - already exists.");
        } else {
            _table.put(((SimpleSymbol)newSym).getPrintName(), newSym);
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
