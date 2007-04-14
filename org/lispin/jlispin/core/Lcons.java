package org.lispin.jlispin.core;

public class Lcons extends Exp {
	
	private final Exp _car;
	private final Exp _cdr;

	public Lcons(Exp car, Exp cdr) {
		_car = car;
		_cdr = cdr;
	}
	public Object getJavaValue() {
		Exp result[] = new Exp[2];
		result[0] = _car;
		result[1] = _cdr;
		return result;
	}
	
	protected String cdrToString() {

		return " " + _car.toString() + _cdr.cdrToString();			
		
	}
	public String toString() {
		
		return "(" + _car.toString() + _cdr.cdrToString() + ")";
	}

}
