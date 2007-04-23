package org.lispin.jlispin.core;

public class Lstring extends Exp {
	
	String _value;

	public Lstring(String str) {
		_value = str;
	}

	public Object getJavaValue() {
		return _value;
	}
	
	public String toString() {
		return "\"" + _value + "\"";
	}
	public void acceptVisitor(Visitor guest) {
		guest.visitLstring(this);
	}


}
