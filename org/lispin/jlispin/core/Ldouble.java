package org.lispin.jlispin.core;

public class Ldouble extends Exp {
	
	private double _value;
	
	public Ldouble(double d) {_value = d;}
	
	public Object getValue() { return new Double(_value); }

}
