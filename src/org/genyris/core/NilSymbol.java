package org.genyris.core;


public class NilSymbol extends Lsymbol {
	
	public NilSymbol() {
		_printName = "nil";
	}
    public boolean isNil() {
    	return true;
    }
    
	public boolean isMember() {
		return false;
	}

	public String toString() {
		return _printName;
	}

	public Object getJavaValue() { 
		return getPrintName(); 
	}

}
