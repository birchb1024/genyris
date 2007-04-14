package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	
	private Map _table;
	
	
	
	public SymbolTable() {
		_table = new HashMap();
	}
	

	public Exp internString(String newSym) {

		if( _table.containsKey(newSym)) {
			return (Exp) _table.get(newSym);
		}
		else {
			Symbol sym = new Symbol(newSym);
			_table.put(newSym, sym);
			return sym; 
		}
	}

}
