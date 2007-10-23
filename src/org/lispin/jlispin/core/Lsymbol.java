package org.lispin.jlispin.core;

import org.genyris.classes.BuiltinClasses;

public class Lsymbol extends ExpWithEmbeddedClasses {
	
	protected String _printName;

	private static int nextgensym = 0;
	
	public Lsymbol(String newSym) {
		super(BuiltinClasses.SYMBOL);
		_printName = newSym;
	}
	
	public Lsymbol(String newSym, boolean bootstrap) {
		_printName = newSym;
	}
	
    public Lsymbol() {
		super(BuiltinClasses.SYMBOL);
		_printName = "G" + nextgensym;
		nextgensym++;
	}
    public boolean isNil() {
    	return false;
    }
    
	public int hashCode() {
    	return getPrintName().hashCode();
    }
	
	public boolean equals(Object compare) {
		return this == compare;
	}
	public String getPrintName() { 
		return _printName; 
	}
	public Object getJavaValue() { 
		return getPrintName(); 
	}
	
	public boolean isSelfEvaluating() {
		return false;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLsymbol(this);
	}

	public boolean isMember() {
		return getPrintName().startsWith(Constants.DYNAMICSCOPECHAR);
	}

	public String toString() {
		return getPrintName();
	}

}
