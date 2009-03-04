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
    private Lsymbol     NIL;

    public SymbolTable() {
        _table = new TreeMap();
    }

    public void init(Lsymbol nil) throws GenyrisException {
        NIL = nil;
        _table.put("nil", NIL);
        _table.put(Constants.SELF, new Lsymbol(Constants.SELF));
        _table.put(Constants.CLASSES, new Lsymbol(Constants.CLASSES));
        _table.put(Constants.SUPERCLASSES, new Lsymbol(Constants.SUPERCLASSES));
        _table.put(Constants.CLASSNAME, new Lsymbol(Constants.CLASSNAME));
        _table.put(Constants.VARS, new Lsymbol(Constants.VARS));
        _table.put(Constants.DYNAMIC_SYMBOL, new Lsymbol(Constants.DYNAMIC_SYMBOL));

    }

    public Lsymbol lookupString(String newSym) throws GenyrisException {
        if (_table.containsKey(newSym)) {
            return (Lsymbol)_table.get(newSym);
        } else {
            throw new GenyrisException("symbol not found in symbol table " + newSym);
        }
    }

    public Lsymbol internString(String news) throws GenyrisException {
        return internPlainString(news);
    }

    public Lsymbol internPlainString(String newSym) {
        if (_table.containsKey(newSym)) {
            return (Lsymbol)_table.get(newSym);
        } else {
            Lsymbol sym = new Lsymbol(newSym);
            _table.put(newSym, sym);
            return sym;
        }
    }

    public void intern(Exp newSym) throws Exception {
        if (_table.containsKey(((Lsymbol)newSym).getPrintName())) {
            throw new GenyrisException("Can't intern symbol - already exists.");
        } else {
            _table.put(((Lsymbol)newSym).getPrintName(), newSym);
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
