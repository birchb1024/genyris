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
	public static Exp lambda;
	public static Exp lambdaq;
	public static Exp quote;
	
	public SymbolTable() {
		_table = new HashMap();
		leftParen = internString("leftParen");
		rightParen = internString("righParen");
		period = internString("period");
		lambda = internString("lambda");
		lambdaq = internString("lambdaq");
		quote = internString("quote");
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
			throw new Exception("Can't intern symbol - already exists.");
		}
		else {
			_table.put(((Lsymbol)newSym).getPrintName(), newSym);
		}
		
	}

}
