package org.lispin.jlispin.core;

import org.lispin.jlispin.classes.BuiltinClasses;


public class Linteger extends ExpWithEmbeddedClasses {
	private int _value;
	
	public Object getJavaValue() { 
		return new Integer(_value); 
	}
	
	public Linteger(int i) {
		super(BuiltinClasses.INTEGER);
		_value = i;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLinteger(this);
	}


}
