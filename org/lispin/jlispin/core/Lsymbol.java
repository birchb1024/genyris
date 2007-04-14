package org.lispin.jlispin.core;

public class Lsymbol extends Exp {
	
	public static final Lsymbol NIL = null;
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
	

}
