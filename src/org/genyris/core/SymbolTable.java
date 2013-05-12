// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.genyris.exception.GenyrisException;

public class SymbolTable implements Internable {
	private Map          _table;
    private SimpleSymbol NIL;
	private SimpleSymbol _self;
	private SimpleSymbol _classes;
	private SimpleSymbol _superclasses;
	private SimpleSymbol _classname;
	private SimpleSymbol _left;
	private SimpleSymbol _right;
    private SimpleSymbol _lineNumber;
    private SimpleSymbol _filename;
    private SimpleSymbol _dynamic;
	private SimpleSymbol _rest;
	private SimpleSymbol _dict;
	private SimpleSymbol _true;
	private SimpleSymbol _false;
	
	private SimpleSymbol _standardclass;
	private SimpleSymbol _subclasses;
	private SimpleSymbol _thing;
	private SimpleSymbol _validate;
	private SimpleSymbol _vars;
	private SimpleSymbol _lambda;
	private SimpleSymbol _lambdaq;
	private SimpleSymbol _lambdam;
	private SimpleSymbol _eof;
	private SimpleSymbol _template;
	private SimpleSymbol _square;
	private SimpleSymbol _curly;
	private SimpleSymbol _quote;
	private SimpleSymbol _comma;
	private SimpleSymbol _comma_at;
	private SimpleSymbol _prefix;
	private SimpleSymbol _BIGNUM;
	private SimpleSymbol _EAGERPROC;
	private SimpleSymbol _LAZYPROC;
	private SimpleSymbol _PAIR;
	private SimpleSymbol _PAIREQUAL;
    private SimpleSymbol _PAIRSOURCE;
    private SimpleSymbol _PARENPARSER;
	private SimpleSymbol _INDENTEDPARSER;
	private SimpleSymbol _READER;
	private SimpleSymbol _SIMPLESYMBOL;
	private SimpleSymbol _STRING;
	private SimpleSymbol _WRITER;
	private SimpleSymbol _dictionary;
	private SimpleSymbol _URISYMBOL;
	private SimpleSymbol _TRIPLE;
	private SimpleSymbol _GRAPH;
    private SimpleSymbol _TYPE;
	private SimpleSymbol _SUBCLASSOF;
	private SimpleSymbol _DYNAMICSYMBOLREF;
	private SimpleSymbol _SOURCE;
	private SimpleSymbol _NAME;
	private SimpleSymbol _SUBJECT;
	private SimpleSymbol _PREDICATE;
	private SimpleSymbol _OBJECT;
	private SimpleSymbol _JAVAWRAPPER;
	private SimpleSymbol _JAVACTOR;
	private SimpleSymbol _JAVAMETHOD;
	private SimpleSymbol _JAVASTATICMETHOD;
	private SimpleSymbol _JAVACLASS;
	private SimpleSymbol _PIPE;
	private SimpleSymbol _PROCEDUREMISSING;
    private SimpleSymbol _GLOBALGRAPH;
    private SimpleSymbol _BISCUIT;
    private SimpleSymbol _TAILCALL;
	
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
      _lineNumber = bindKeyword(Constants.LINENUMBER);
      _filename = bindKeyword(Constants.FILENAME);
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
      _square = bindKeyword(Constants.SQUARE);
      _curly = bindKeyword(Constants.CURLY);
      _quote = bindKeyword(Constants.QUOTE);
      _comma = bindKeyword(String.valueOf(Constants.COMMA));
      _comma_at = bindKeyword(String.valueOf(Constants.COMMA_AT));
      _prefix = bindKeyword(String.valueOf(Constants.PREFIX));

  	_BIGNUM = bindKeyword(String.valueOf(Constants.BIGNUM));
	_EAGERPROC = bindKeyword(String.valueOf(Constants.EAGERPROCEDURE));
	_LAZYPROC = bindKeyword(String.valueOf(Constants.LAZYPROCEDURE));
	_PAIR = bindKeyword(String.valueOf(Constants.PAIR));
	_PAIREQUAL = bindKeyword(String.valueOf(Constants.PAIREQUAL));
    _PAIRSOURCE = bindKeyword(String.valueOf(Constants.PAIRESOURCE));

	_PARENPARSER = bindKeyword(String.valueOf(Constants.PARENPARSER));
	_INDENTEDPARSER = bindKeyword(String.valueOf(Constants.INDENTEDPARSER));
	_READER = bindKeyword(String.valueOf(Constants.READER));
	_SIMPLESYMBOL = bindKeyword(String.valueOf(Constants.SIMPLESYMBOL));
	_URISYMBOL = bindKeyword(String.valueOf(Constants.URISYMBOL));
	_STRING = bindKeyword(String.valueOf(Constants.STRING));
	_WRITER = bindKeyword(String.valueOf(Constants.WRITER));

	_TRIPLE = bindKeyword(String.valueOf(Constants.TRIPLE));
	_GRAPH = bindKeyword(String.valueOf(Constants.GRAPH));
    _TYPE = bindKeyword(String.valueOf(Constants.TYPE));
    _GLOBALGRAPH = bindKeyword(String.valueOf(Constants.GLOBALGRAPH));
    
    _SUBCLASSOF = bindKeyword(String.valueOf(Constants.SUBCLASSOF));
    _DYNAMICSYMBOLREF = bindKeyword(String.valueOf(Constants.DYNAMICSYMBOLREF));
    _NAME = bindKeyword(String.valueOf(Constants.NAME));
    _SOURCE = bindKeyword(String.valueOf(Constants.SOURCE));

    _SUBJECT = bindKeyword(String.valueOf(Constants.SUBJECT));
    _PREDICATE = bindKeyword(String.valueOf(Constants.PREDICATE));
    _OBJECT = bindKeyword(String.valueOf(Constants.OBJECT));
    _JAVAWRAPPER = bindKeyword(String.valueOf(Constants.JAVAWRAPPER));
    _JAVACTOR = bindKeyword(String.valueOf(Constants.JAVACTOR));
    _JAVAMETHOD = bindKeyword(String.valueOf(Constants.JAVAMETHOD));
    _JAVASTATICMETHOD = bindKeyword(String.valueOf(Constants.JAVASTATICMETHOD));
    _JAVACLASS = bindKeyword(String.valueOf(Constants.JAVACLASS));
    _PIPE = bindKeyword(String.valueOf(Constants.PIPE));
    _PROCEDUREMISSING = bindKeyword(String.valueOf(Constants.PROCEDUREMISSING));
    _BISCUIT = bindKeyword(String.valueOf(Constants.BISCUIT));
    _TAILCALL = bindKeyword(String.valueOf(Constants.TAILCALL));

}

	private SimpleSymbol bindKeyword(String name) {
		SimpleSymbol sym = new SimpleSymbol(name);
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

    public SimpleSymbol internString(String newSym) {
        if (_table.containsKey(newSym)) {
            return (SimpleSymbol)_table.get(newSym);
        } else {
            SimpleSymbol sym = new SimpleSymbol(newSym);
            _table.put(newSym, sym);
            return sym;
        }
    }

    public SimpleSymbol internSymbol(SimpleSymbol newSym) {
        if (_table.containsKey(newSym.getPrintName())) {
            return (SimpleSymbol)_table.get(newSym.getPrintName());
        } else {
            _table.put(newSym.getPrintName(), newSym);
            return newSym;
        }
    }

    public SimpleSymbol NIL() {
        return NIL;
    }

    public Exp getSymbolsList() {
        Pair head, tail;
        Iterator iter = _table.values().iterator();
        Exp key = (Exp)iter.next(); // Safe to assume symbol table is never empty
        head = tail = new Pair(key,NIL);
        while(iter.hasNext()) {
            key = (Exp)iter.next();
            Pair newItem = new Pair(key,NIL);
            tail.setCdr(newItem);
            tail = newItem;
        }
        return head;
    }

    public List<Exp> getSymbolsAsListOfExp() {
        ArrayList<Exp> retval = new ArrayList<Exp>();
        retval.addAll(_table.values());
        return retval;
    }

    public List<String> getSymbolsListAsListOfStrings() {
        ArrayList<String> retval = new ArrayList<String>();
        retval.addAll(_table.keySet());
        return retval;
    }

    //
    // The following methods exist to eliminate use of internString in clients.
    // Added after performance tests.
    //
	public SimpleSymbol SELF() {
		return _self;
	}

	public SimpleSymbol CLASSES() {
		return _classes;
	}

	public SimpleSymbol CLASSNAME() {
		return _classname;
	}

	public SimpleSymbol DYNAMIC_SYMBOL() {
		return _dynamic;
	}

	public SimpleSymbol LEFT() {
		return _left;
	}

	public SimpleSymbol RIGHT() {
		return _right;
	}

	public SimpleSymbol SUPERCLASSES() {
		return _superclasses;
	}

	public SimpleSymbol REST() {
		return _rest;
	}

	public SimpleSymbol DICT() {
		return _dict;
	}

	public SimpleSymbol DICTIONARY() {
		return _dictionary;
	}

	public SimpleSymbol TRUE() {
		return _true;
	}

	public SimpleSymbol FALSE() {
		return _false;
	}
	public SimpleSymbol VARS() {
		return _vars;
	}

	public SimpleSymbol STANDARDCLASS() {
		return _standardclass;
	}

	public SimpleSymbol SUBCLASSES() {
		return _subclasses;
	}

	public SimpleSymbol THING() {
		return _thing;
	}

	public SimpleSymbol VALIDATE() {
		return _validate;
	}

	public SimpleSymbol LAMBDA() {
		return _lambda;
	}

	public SimpleSymbol LAMBDAM() {
		return _lambdam;
	}

	public SimpleSymbol LAMBDAQ() {
		return _lambdaq;
	}

	public SimpleSymbol EOF() {
		return _eof;
	}
	public SimpleSymbol SQUARE() {
		return _square;
	}
	public SimpleSymbol CURLY() {
		return _curly;
	}
	public SimpleSymbol TEMPLATE() {
		return _template;
	}
	public SimpleSymbol QUOTE() {
		return _quote;
	}

	public SimpleSymbol COMMA() {
		return _comma;
	}
	public SimpleSymbol COMMA_AT() {
		return _comma_at;
	}

	public SimpleSymbol PREFIX() {
		return _prefix;
	}

	public SimpleSymbol BIGNUM() {
		return _BIGNUM;
	}

	public SimpleSymbol EAGERPROC() {
		return _EAGERPROC;
	}

	public SimpleSymbol LAZYPROC() {
		return _LAZYPROC;
	}

	public SimpleSymbol PAIR() {
		return _PAIR;
	}

	public SimpleSymbol PAIREQUAL() {
		return _PAIREQUAL;
	}

    public SimpleSymbol PAIRSOURCE() {
        return _PAIRSOURCE;
    }

	public SimpleSymbol PARENPARSER() {
		return _PARENPARSER;
	}

	public SimpleSymbol READER() {
		return _READER;
	}

	public SimpleSymbol SIMPLESYMBOL() {
		return _SIMPLESYMBOL;
	}

	public SimpleSymbol URISYMBOL() {
		return _URISYMBOL;
	}

	public SimpleSymbol STRING() {
		return _STRING;
	}

	public SimpleSymbol WRITER() {
		return _WRITER;
	}
	public SimpleSymbol GRAPH() {
		return _GRAPH;
	}
	public SimpleSymbol TRIPLE() {
		return _TRIPLE;
	}

    public SimpleSymbol TYPE() {
        return _TYPE;
    }

	public SimpleSymbol SUBCLASSOF() {
		return _SUBCLASSOF;
	}

	public SimpleSymbol DYNAMICSYMBOLREF() {
		return _DYNAMICSYMBOLREF;
	}
	public SimpleSymbol SOURCE() {
		return _SOURCE;
	}
	public SimpleSymbol NAME() {
		return _NAME;
	}
	public SimpleSymbol SUBJECT() {
		return _SUBJECT;
	}
	public SimpleSymbol PREDICATE() {
		return _PREDICATE;
	}
	public SimpleSymbol OBJECT() {
		return _OBJECT;
	}
	public SimpleSymbol JAVAWRAPPER() {
		return _JAVAWRAPPER;
	}

	public SimpleSymbol JAVACTOR() {
		return _JAVACTOR;
	}

	public SimpleSymbol JAVAMETHOD() {
		return _JAVAMETHOD;
	}

	public SimpleSymbol JAVASTATICMETHOD() {
		return _JAVASTATICMETHOD;
	}

	public SimpleSymbol JAVACLASS() {
		return _JAVACLASS;
	}

	public SimpleSymbol PIPE() {
		return _PIPE;
	}

	public SimpleSymbol INDENTPARSER() {
		return _INDENTEDPARSER;
	}

	public SimpleSymbol PROCEDUREMISSING() {
		return _PROCEDUREMISSING;
	}

    public SimpleSymbol GLOBALGRAPH() {
        return _GLOBALGRAPH;
    }

    public SimpleSymbol LINENUMBER() {
        return _lineNumber;
    }

    public SimpleSymbol FILENAME() {
        return _filename;
    }

    public SimpleSymbol BISCUIT() {
        return _BISCUIT;
    }

    public SimpleSymbol TAILCALL() {
        return _TAILCALL;
    }

}
