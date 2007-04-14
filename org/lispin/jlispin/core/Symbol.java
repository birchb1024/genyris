package org.lispin.jlispin.core;

public class Symbol extends Exp {
	
	public static final Symbol NIL = null;
	private String _printName;
	
	public Symbol(String newSym) {
		_printName = newSym;
	}
	
    public int hashCode() {
    	return _printName.hashCode();
    }

	public Object getJavaValue() { 
		return _printName; 
	}
	

}
