package org.lispin.jlispin.core;

public class Lsymbol extends ExpWithEmbeddedClasses {
	
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
	
	public boolean equals(Object compare) {
		return this == compare;
	}
	public String getPrintName() { 
		return _printName; 
	}
	public Object getJavaValue() { 
		return _printName; 
	}
	
	public boolean isSelfEvaluating() {
		return false;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLsymbol(this);
	}

	public boolean isMember() {
		return _printName.startsWith(SymbolTable.DYNAMICSCOPECHAR);
	}

}
