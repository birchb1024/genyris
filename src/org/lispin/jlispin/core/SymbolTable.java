package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Map;
import org.lispin.jlispin.interp.LispinException;

public class SymbolTable {
	
	// TODO refactor and push these 'optimisations' into the user classes. 
	// ie use lookup() instead of a static, except in using classes.
	public static final String DYNAMICSCOPECHAR = "_";
    private Map _table;
    private Lsymbol NIL;
    
    public static Lsymbol leftParen;
    public static Lsymbol rightParen;
    public static Lsymbol cdr_char;
    public static Lsymbol EOF;
    public static Lsymbol closure;
    public static Lsymbol self;
    public static Lsymbol _self;
    public static Lsymbol lambda;
    public static Lsymbol lambdaq;
    public static Lsymbol lambdam;
    public static Lsymbol method;
    public static Lsymbol quote;
    public static Lsymbol raw_quote;
    public static Lsymbol backquote;
    public static Lsymbol raw_backquote;
    public static Lsymbol raw_comma_at;
    public static Lsymbol raw_comma;
    public static Lsymbol comma;
    public static Lsymbol comma_at;
    public static Lsymbol REST;
    public static Lsymbol DICT;
    public static Lsymbol classes;
    public static Lsymbol superclasses;
	public static Lsymbol classname;
	public static Lsymbol _validate;

    public SymbolTable() {
        _table = new HashMap();
    }
    
    public void init(Lsymbol nil) {
    	NIL = nil;
        _table.put("nil", NIL);
        leftParen = internString("leftParen");
        rightParen = internString("righParen");
        cdr_char = internString("pair-delimiter");
        closure = internString("closure");
        self = internString("self");
        lambda = internString("lambda");
        lambdaq = internString("lambdaq");
        lambdam = internString("lambdam");
        method = internString("method");
        quote = internString("quote");
        raw_quote = internString("'");
        raw_backquote = internString("`");
        raw_comma_at = internString(",@");
        raw_comma = internString(",");
        comma_at = internString("comma-at");
        comma = internString("comma");
        backquote = internString("backquote");
        EOF = internString("EOF");
        REST = internString("&rest");
        DICT = internString("dict");
        classes = internString(DYNAMICSCOPECHAR + "classes");
        superclasses = internString(DYNAMICSCOPECHAR + "superclasses");
        classname = internString(DYNAMICSCOPECHAR + "classname"); // Needed by Lsymbol constructor
        _self = internString(DYNAMICSCOPECHAR + "self");
        _validate = internString(DYNAMICSCOPECHAR + "validate");
    }

    public Lsymbol internString(String newSym) {
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
            throw new LispinException("Can't intern symbol - already exists.");
        } else {
            _table.put(((Lsymbol)newSym).getPrintName(), newSym);
        }
    }
    
    public Exp getNil() {
    	return NIL;
    }
}
