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
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.PrefixMapper;

public class SymbolTable {
    private Map         _table;
    private Lsymbol     NIL;
    private Interpreter _interp;
    private Environment _globalEnv;
    public PrefixMapper _prefixes; //TODO public temporarily

    public SymbolTable(Interpreter interp) {
        _table = new TreeMap();
        _prefixes = new PrefixMapper();
        _interp = interp;
        if (_interp != null) {
            _globalEnv = _interp.getGlobalEnv();
        }
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

        ((Lsymbol)_table.get(Constants.SELF)).initFromTable(this);
        ((Lsymbol)_table.get(Constants.CLASSES)).initFromTable(this);
        ((Lsymbol)_table.get(Constants.SUPERCLASSES)).initFromTable(this);
        ((Lsymbol)_table.get(Constants.CLASSNAME)).initFromTable(this);
        ((Lsymbol)_table.get(Constants.DYNAMIC_SYMBOL)).initFromTable(this);
    }

    public Lsymbol lookupString(String news) throws GenyrisException {
        String newSym = this._prefixes.getCannonicalSymbol(news);
        return lookupPlainString(newSym);
    }

    public Lsymbol lookupPlainString(String newSym) throws GenyrisException {
        if (_table.containsKey(newSym)) {
            return (Lsymbol)_table.get(newSym);
        } else {
            throw new GenyrisException("symbol not found in symbol table " + newSym);
        }
    }

    public Lsymbol internString(String news) throws GenyrisException {
        String newSym = this._prefixes.getCannonicalSymbol(news);
        return internPlainString(newSym);
    }

    public Lsymbol internPlainString(String newSym) {
        if (_table.containsKey(newSym)) {
            return (Lsymbol)_table.get(newSym);
        } else {
            Lsymbol sym = new Lsymbol(newSym);
            try {
                sym.initFromTable(this);
                sym.setParent(_globalEnv);
            }
            catch (GenyrisException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.exit(-1);
            }
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


    public void initEnvironment(Environment environment) {
        this._globalEnv = environment;

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
