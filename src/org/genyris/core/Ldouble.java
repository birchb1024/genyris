package org.genyris.core;

import org.genyris.classes.BuiltinClasses;

public class Ldouble extends ExpWithEmbeddedClasses {
	
	private double _value;
	
	public Ldouble(double d) {
		super(BuiltinClasses.DOUBLE);
		_value = d;}
	
	public Object getJavaValue() { return new Double(_value); }

	public void acceptVisitor(Visitor guest) {
		guest.visitLdouble(this);
	}

	public String toString() {
		return getJavaValue().toString();
	}


}
