package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.SymbolTable;

public class Interpreter {
	
	Environment _globalEnvironment;
	
	public Interpreter() {
		_globalEnvironment = new Environment(null);
		_globalEnvironment.defineVariable(SymbolTable.NIL, SymbolTable.NIL);
	}
	

}
