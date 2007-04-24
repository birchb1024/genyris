package org.lispin.jlispin.core;


public class Lcons extends Exp {
	
	private  Exp _car;
	private  Exp _cdr;

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

	public void acceptVisitor(Visitor guest) {
		guest.visitLcons(this);
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
