package org.lispin.jlispin.core;

import org.genyris.classes.BuiltinClasses;

public class Lstring extends ExpWithEmbeddedClasses {
	
	String _value;

	public Lstring(String str) {
		super(BuiltinClasses.STRING);
		_value = str;
	}

	public Object getJavaValue() {
		return _value;
	}
	
	public String toString() {
		return _value;
	}
	public void acceptVisitor(Visitor guest) {
		guest.visitLstring(this);
	}


}
