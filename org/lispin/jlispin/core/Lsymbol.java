package org.lispin.jlispin.core;

public class Lsymbol extends Exp {
	
	private String _printName;
	private Exp _symbolValue;
	private static int nextgensym = 0;
	
	public Lsymbol(String newSym) {
		_printName = newSym;
	}
	
    public Lsymbol() {
		_printName = "G" + nextgensym;
		nextgensym++;
	}

	public int hashCode() {
    	return _printName.hashCode();
    }

	public Object getPrintName() { 
		return _printName; 
	}
	public Object getJavaValue() { 
		return getPrintName(); 
	}

	protected String cdrToString() {
		if( this == SymbolTable.NIL ) 
			return "";
		return " . " + cdrToString();
	}

	public Exp set(Exp valu) {
		_symbolValue = valu;
		return valu;
	}

	public Exp get() {
		return _symbolValue;
	}
}
