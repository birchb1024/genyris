// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.genyris.exception.GenyrisException;

public class SymbolTable implements Internable {
    private Map         _table;
    private SimpleSymbol     NIL;
	private Symbol _self;
	private Symbol _classes;
	private Symbol _superclasses;
	private Symbol _classname;
	private Symbol _left;
	private Symbol _right;
	private Symbol _dynamic;
	private Symbol _rest;
	private Symbol _dict;
	private Exp _true;
	private Exp _false;
	
	private Exp _standardclass;
	private Exp _subclasses;
	private Exp _thing;
	private Symbol _validate;
	private Exp _vars;

    public SymbolTable() {
        _table = new HashMap();
    }

    public void init(SimpleSymbol nil) {
        NIL = nil;
        _table.put(Constants.NIL, NIL);
      _self = bindKeyword(Constants.SELF);
      _classes = bindKeyword(Constants.CLASSES);
      _superclasses = bindKeyword(Constants.SUPERCLASSES);
      _classname = bindKeyword(Constants.CLASSNAME);
      _left = bindKeyword(Constants.LEFT);
      _right = bindKeyword(Constants.RIGHT);
      _dynamic = bindKeyword(Constants.DYNAMIC_SYMBOL);
      _rest = bindKeyword(Constants.REST);
      _dict = bindKeyword(Constants.DICT);
      _true = bindKeyword(Constants.TRUE);
      _false = bindKeyword(Constants.FALSE);
      _standardclass = bindKeyword(Constants.STANDARDCLASS);
      _subclasses = bindKeyword(Constants.SUBCLASSES);
      _superclasses = bindKeyword(Constants.SUPERCLASSES);
      _thing = bindKeyword(Constants.THING);
      _validate = bindKeyword(Constants.VALIDATE);
      _vars = bindKeyword(Constants.VARS);

    }

	private Symbol bindKeyword(String name) {
		Symbol sym = new SimpleSymbol(name);
		_table.put(name, sym);
		return sym;
	}

    public SimpleSymbol lookupString(String newSym) throws GenyrisException {
        if (_table.containsKey(newSym)) {
            return (SimpleSymbol)_table.get(newSym);
        } else {
            throw new GenyrisException("symbol not found in symbol table " + newSym);
        }
    }

    public Symbol internString(String newSym) {
    	System.out.println("intern: " + newSym);
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

	public Symbol SELF() {
		return _self;
	}

	public Symbol CLASSES() {
		return _classes;
	}

	public Symbol CLASSNAME() {
		return _classname;
	}

	public Symbol DYNAMIC_SYMBOL() {
		return _dynamic;
	}

	public Symbol LEFT() {
		return _left;
	}

	public Symbol RIGHT() {
		return _right;
	}

	public Symbol SUPERCLASSES() {
		return _superclasses;
	}

	public Symbol REST() {
		return _rest;
	}

	public Exp DICT() {
		return _dict;
	}

	public Exp TRUE() {
		return _true;
	}

	public Exp FALSE() {
		return _false;
	}
	public Exp VARS() {
		return _vars;
	}

	public Exp STANDARDCLASS() {
		return _standardclass;
	}

	public Exp SUBCLASSES() {
		return _subclasses;
	}

	public Exp THING() {
		return _thing;
	}

	public Symbol VALIDATE() {
		return _validate;
	}
}
