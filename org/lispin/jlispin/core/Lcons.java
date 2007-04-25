package org.lispin.jlispin.core;

import org.lispin.jlispin.interp.LispinException;


public class Lcons extends Exp {
	
	private  Exp _car;
	private  Exp _cdr;

	public Lcons(Exp car, Exp cdr) throws LispinException {
		if( car == null )
			throw new LispinException("null pointer in cons(car)");
		if( cdr == null )
			throw new LispinException("null pointer in cons(cdr)");
		_car = car;
		_cdr = cdr;
	}
	public Object getJavaValue() {
		Exp result[] = new Exp[2];
		result[0] = _car;
		result[1] = _cdr;
		return result;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLcons(this);
	}
	public int hashCode() {
    	return (_car.hashCode() + _car.hashCode()%32000);
    }

	public boolean equals(Object compare) {
		if( compare.getClass() != this.getClass())
			return false;
		else {
			Lcons comp = (Lcons)compare;
			return _car.equals(comp.car()) && _cdr.equals(comp.cdr());
		}
	}

	public Exp car() {
		return _car;
	}

	public Exp cdr() {
		return _cdr;
	}
	
	public Exp setCar(Exp exp) throws AccessException {
		this._car = exp;;
		return this;
	}

	public Exp setCdr(Exp exp) throws AccessException {
		this._cdr = exp;;
		return this;
	}	

	public boolean isSelfEvaluating() {
		return false;
	}
}
