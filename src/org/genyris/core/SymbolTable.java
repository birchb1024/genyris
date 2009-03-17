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
	private Symbol _true;
	private Symbol _false;
	
	private Symbol _standardclass;
	private Symbol _subclasses;
	private Symbol _thing;
	private Symbol _validate;
	private Symbol _vars;
	private Symbol _lambda;
	private Symbol _lambdaq;
	private Symbol _lambdam;
	private Symbol _eof;
	private Symbol _template;
	private Symbol _quote;
	private Symbol _comma;
	private Symbol _comma_at;
	private Symbol _prefix;
	private Symbol _BIGNUM;
	private Symbol _EAGERPROC;
	private Symbol _LAZYPROC;
	private Symbol _PAIR;
	private Symbol _PARENPARSER;
	private Symbol _READER;
	private Symbol _SIMPLESYMBOL;
	private Symbol _STRING;
	private Symbol _WRITER;
	private Symbol _dictionary;
	private Symbol _URISYMBOL;
	private Symbol _TRIPLE;
	private Symbol _TRIPLESET;
    private Symbol _TYPE;

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
      _dictionary = bindKeyword(Constants.DICTIONARY);
      _true = bindKeyword(Constants.TRUE);
      _false = bindKeyword(Constants.FALSE);
      _standardclass = bindKeyword(Constants.STANDARDCLASS);
      _subclasses = bindKeyword(Constants.SUBCLASSES);
      _superclasses = bindKeyword(Constants.SUPERCLASSES);
      _thing = bindKeyword(Constants.THING);
      _validate = bindKeyword(Constants.VALIDATE);
      _vars = bindKeyword(Constants.VARS);

      _lambda = bindKeyword(Constants.LAMBDA);
      _lambdaq = bindKeyword(Constants.LAMBDAQ);
      _lambdam = bindKeyword(Constants.LAMBDAM);

      _eof = bindKeyword(Constants.EOF);
      _template = bindKeyword(Constants.TEMPLATE);
      _quote = bindKeyword(Constants.QUOTE);
      _comma = bindKeyword(String.valueOf(Constants.COMMA));
      _comma_at = bindKeyword(String.valueOf(Constants.COMMA_AT));
      _prefix = bindKeyword(String.valueOf(Constants.PREFIX));

  	_BIGNUM = bindKeyword(String.valueOf(Constants.BIGNUM));
	_EAGERPROC = bindKeyword(String.valueOf(Constants.EAGERPROCEDURE));
	_LAZYPROC = bindKeyword(String.valueOf(Constants.LAZYPROCEDURE));
	_PAIR = bindKeyword(String.valueOf(Constants.PAIR));

	_PARENPARSER = bindKeyword(String.valueOf(Constants.PARENPARSER));
	_READER = bindKeyword(String.valueOf(Constants.READER));
	_SIMPLESYMBOL = bindKeyword(String.valueOf(Constants.SIMPLESYMBOL));
	_URISYMBOL = bindKeyword(String.valueOf(Constants.URISYMBOL));
	_STRING = bindKeyword(String.valueOf(Constants.STRING));
	_WRITER = bindKeyword(String.valueOf(Constants.WRITER));

	_TRIPLE = bindKeyword(String.valueOf(Constants.TRIPLE));
	_TRIPLESET = bindKeyword(String.valueOf(Constants.TRIPLESET));
    _TYPE = bindKeyword(String.valueOf(Constants.TYPE));
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

    public Symbol NIL() {
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
    
    //
    // The following methods exist to eliminate use of internString in clients.
    // Added after performance tests.
    //
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

	public Symbol DICT() {
		return _dict;
	}

	public Symbol DICTIONARY() {
		return _dictionary;
	}

	public Symbol TRUE() {
		return _true;
	}

	public Symbol FALSE() {
		return _false;
	}
	public Symbol VARS() {
		return _vars;
	}

	public Symbol STANDARDCLASS() {
		return _standardclass;
	}

	public Symbol SUBCLASSES() {
		return _subclasses;
	}

	public Symbol THING() {
		return _thing;
	}

	public Symbol VALIDATE() {
		return _validate;
	}

	public Symbol LAMBDA() {
		return _lambda;
	}

	public Symbol LAMBDAM() {
		return _lambdaq;
	}

	public Symbol LAMBDAQ() {
		return _lambdam;
	}

	public Symbol EOF() {
		return _eof;
	}
	public Symbol TEMPLATE() {
		return _template;
	}
	public Symbol QUOTE() {
		return _quote;
	}

	public Symbol COMMA() {
		return _comma;
	}
	public Symbol COMMA_AT() {
		return _comma_at;
	}

	public Symbol PREFIX() {
		return _prefix;
	}

	public Symbol BIGNUM() {
		return _BIGNUM;
	}

	public Symbol EAGERPROC() {
		return _EAGERPROC;
	}

	public Symbol LAZYPROC() {
		return _LAZYPROC;
	}

	public Symbol PAIR() {
		return _PAIR;
	}

	public Symbol PARENPARSER() {
		return _PARENPARSER;
	}

	public Symbol READER() {
		return _READER;
	}

	public Symbol SIMPLESYMBOL() {
		return _SIMPLESYMBOL;
	}

	public Symbol URISYMBOL() {
		return _URISYMBOL;
	}

	public Symbol STRING() {
		return _STRING;
	}

	public Symbol WRITER() {
		return _WRITER;
	}
	public Symbol TRIPLESET() {
		return _TRIPLESET;
	}
	public Symbol TRIPLE() {
		return _TRIPLE;
	}

    public Symbol TYPE() {
        return _TYPE;
    }
}
