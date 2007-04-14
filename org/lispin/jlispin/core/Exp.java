package org.lispin.jlispin.core;

public abstract class Exp {
	
	public boolean equals(Object compare) {
		if( compare.getClass() == this.getClass()) {
			return this.getJavaValue().equals(((Exp)compare).getJavaValue());
		}
		else {
			return false;
		}
	}
	
	public abstract Object getJavaValue();
	
	public String toString() {
		return getJavaValue().toString();
	}

    //-----------------------
	
	public boolean isNil() {
		return this == SymbolTable.NIL;
	}
	
	protected String cdrToString() {
		return " . " + cdrToString();
	}
}
