package org.lispin.jlispin.core;

import java.io.StringWriter;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.format.BasicFormatter;

public class Lcons extends ExpWithEmbeddedClasses {

	private  Exp _car;
	private  Exp _cdr;

	public Lcons(Exp car, Exp cdr) {
		super(BuiltinClasses.PAIR);
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

    public boolean deepEquals(Object compare) {
        if (compare.getClass() != this.getClass())
            return false;
        else
            return this._car.deepEquals(((Lcons)compare)._car)
                && this._cdr.deepEquals(((Lcons)compare)._cdr);
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

	public String toString() {
		StringWriter out = new StringWriter();
		acceptVisitor(new BasicFormatter(out));
		return out.getBuffer().toString();
	}

}
