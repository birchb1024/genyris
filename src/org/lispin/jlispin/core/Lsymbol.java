package org.lispin.jlispin.core;

import org.lispin.jlispin.classes.BuiltinClasses;

public class Lsymbol extends ExpWithEmbeddedClasses {
	
	private String _printName;

	private static int nextgensym = 0;
	
	public Lsymbol(String newSym) {
		super(BuiltinClasses.SYMBOL);
		_printName = newSym;
	}
	
    public Lsymbol() {
		super(BuiltinClasses.SYMBOL);
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
