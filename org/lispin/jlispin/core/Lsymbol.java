package org.lispin.jlispin.core;

public class Lsymbol extends Exp {
	
	private String _printName;
	
	public Lsymbol(String newSym) {
		_printName = newSym;
	}
	
    public int hashCode() {
    	return _printName.hashCode();
    }

	public Object getJavaValue() { 
		return _printName; 
	}

	protected String cdrToString() {
		if( this == SymbolTable.NIL ) 
			return "";
		return " . " + cdrToString();
	}

}
