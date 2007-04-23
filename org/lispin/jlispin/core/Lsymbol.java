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
	
	public boolean isSelfEvaluating() {
		return false;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLsymbol(this);
	}

}
