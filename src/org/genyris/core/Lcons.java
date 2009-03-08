// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.io.StringWriter;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.format.AbstractFormatter;
import org.genyris.format.BasicFormatter;

public class Lcons extends ExpWithEmbeddedClasses {

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

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitLcons(this);
    }

    public boolean equals(Object compare) {
        if (compare.getClass() != this.getClass())
            return false;
        else
            return this._car.equals(((Lcons)compare)._car)
                && this._cdr.equals(((Lcons)compare)._cdr);
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

    public Exp setCdr(Exp exp) {
        this._cdr = exp;;
        return this;
    }

    public boolean isSelfEvaluating() {
        return false;
    }

    public String toString() {
        StringWriter buffer = new StringWriter();
        AbstractFormatter formatter = new BasicFormatter(buffer);
        try {
			this.acceptVisitor(formatter);
		} catch (GenyrisException e) {
			return e.getMessage();
		}
        return buffer.toString();
    }
    public String getBuiltinClassName() {
        return Constants.PAIR;
    }

}
