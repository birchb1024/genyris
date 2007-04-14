package org.lispin.jlispin.core;

public class Lsymbol extends Exp {
	
	private String _printName;

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

	public String getPrintName() { 
		return _printName; 
	}
	public Object getJavaValue() { 
		return getPrintName(); 
	}

	protected String cdrToString() {
		if( this == SymbolTable.NIL ) 
			return "";
		return " . " + toString();
	}
	
	public boolean isSelfEvaluating() {
		return false;
	}

}
