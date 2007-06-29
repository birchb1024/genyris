package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.interp.LispinException;

public class SymbolTable {
	
	private Map _table;
	
	public static Exp NIL;
	public static Exp T;
	public static Exp leftParen;
	public static Exp rightParen;
	public static Exp period;
	public static Exp EOF;
	public static Exp closure;
	public static Exp self;
	public static Exp lambda;
	public static Exp lambdaq;
	public static Exp lambdam;
	public static Exp quote;
	public static Exp raw_quote;
	public static Exp backquote;
	public static Exp raw_comma_at;
	public static Exp raw_comma;
	public static Exp comma;
	public static Exp comma_at;
	public static Exp REST;
	public static Exp DICT;
	
	public SymbolTable() {
		_table = new HashMap();
		leftParen = internString("leftParen");
		rightParen = internString("righParen");
		period = internString("period");
		closure = internString("closure");
		self = internString("self");
		lambda = internString("lambda");
		lambdaq = internString("lambdaq");
		lambdam = internString("lambdam");
		quote = internString("quote");
		raw_quote = internString("'");
		raw_comma_at = internString(",@");
		raw_comma = internString(",");
		comma_at = internString("comma-at"); 
		comma = internString("comma");
		backquote = internString("backquote");
		EOF = internString("EOF");
		NIL = internString("nil");
		T = internString("t");
		REST = internString("&rest");		
		DICT = internString("dict");
	}
	

	public Exp internString(String newSym) {

		if( _table.containsKey(newSym)) {
			return (Exp) _table.get(newSym);
		}
		else {
			Lsymbol sym = new Lsymbol(newSym);
			_table.put(newSym, sym);
			return sym; 
		}
	}


	public void intern(Exp newSym) throws Exception {
		if( _table.containsKey(((Lsymbol)newSym).getPrintName())) {
			throw new LispinException("Can't intern symbol - already exists.");
		}
		else {
			_table.put(((Lsymbol)newSym).getPrintName(), newSym);
		}
		
	}

}
