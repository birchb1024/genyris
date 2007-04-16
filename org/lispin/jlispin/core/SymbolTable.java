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
	public static Exp lambda;
	public static Exp lambdaq;
	public static Exp lambdam;
	public static Exp quote;
	public static Exp raw_quote;
	public static Exp backquote;

	
	public SymbolTable() {
		_table = new HashMap();
		leftParen = internString("leftParen");
		rightParen = internString("righParen");
		period = internString("period");
		lambda = internString("lambda");
		lambdaq = internString("lambdaq");
		lambdam = internString("lambdam");
		quote = internString("quote");
		raw_quote = internString("'");
		backquote = internString("backquote");
		EOF = internString("EOF");
		NIL = internString("nil");
		T = internString("t");
		
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
