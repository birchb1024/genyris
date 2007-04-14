package org.lispin.jlispin.core;

public class Linteger extends Exp {
	private int _value;
	
	public Object getValue() { return new Integer(_value); }
	
	public Linteger(int i) {
		_value = i;
	}
}
