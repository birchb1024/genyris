package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	

	private Map _table;
	
	public static Exp NIL;
	public static Exp T;
	public static Exp leftParen;
	public static Exp rightParen;
	public static Exp period;
	public static Exp EOF;
	
	public SymbolTable() {
		_table = new HashMap();
		leftParen = internString("leftParen");
		rightParen = internString("righParen");
		period = internString("period");
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

}
