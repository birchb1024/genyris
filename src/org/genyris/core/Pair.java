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
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.PairEnvironment;

public class Pair extends ExpWithEmbeddedClasses {

    private  Exp _car;
    private  Exp _cdr;

    public Pair(Exp car, Exp cdr) {
        _car = car;
        _cdr = cdr;
    }
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.PAIR();
	}

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitPair(this);
    }

    public boolean equals(Object compare) {
        if (!(compare instanceof Pair))
            return false;
        else
            return this._car.equals(((Pair)compare)._car)
                && this._cdr.equals(((Pair)compare)._cdr);
    }

	public boolean isPair() {
		return true;
	}

	public Exp car() {
        return _car;
    }

    public Exp cdr() {
        return _cdr;
    }

    public Exp setCar(Exp exp) {
        this._car = exp;;
        return this;
    }

    public Exp setCdr(Exp exp) {
        this._cdr = exp;;
        return this;
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
	public int hashCode() {
		return _car.hashCode() + _cdr.hashCode();
	}
	public Exp eval(Environment env) throws GenyrisException {
        Exp tmp = car().eval(env);
        if(!(tmp instanceof Closure)) {
            throw new GenyrisException("Attempt to call something which is not a Closure: "
                    + toString());
        }
        Closure proc = (Closure) tmp;
        Exp[] arguments = proc.computeArguments(env, cdr());
        return proc.applyFunction(env, arguments);      
	} 
	
    public Exp evalSequence(Environment env) throws GenyrisException {
        SimpleSymbol NIL = env.getNil();
        Exp body = this;
        if (body.cdr() == NIL) {
            return body.car().eval(env);
        }
        else {
            body.car().eval(env);
            return body.cdr().evalSequence(env);
        }
    }
	public int length(Symbol NIL) throws AccessException {
		Exp tmp = this;
		int count = 0;

		while (tmp != NIL && (tmp instanceof Pair)) {
			tmp = tmp.cdr();
			count++;
		}
//		if (tmp != NIL && !(tmp instanceof Pair)) {
//			count++;
//		}
		return count;
	}
	public Exp nth(int number, Symbol NIL) throws AccessException {
		Exp tmp = this;
		int count = 0;
		while (tmp != NIL) {
			if (count == number) {
				return tmp.car();
			}
			tmp = tmp.cdr();
			count++;
		}
		throw new AccessException("nth could not find item: " + number);
	}
	public Environment makeEnvironment(Environment parent) throws GenyrisException {
		return new PairEnvironment(parent, this);
	}
	public static Exp reverse(Exp list, Exp NIL) throws GenyrisException {
		if(list.isNil()) {
			return list;
		}
		if(list instanceof Pair) {
			Exp rev_result = NIL;

			while (list != NIL) {
				rev_result = new Pair(list.car(), rev_result);
				list = list.cdr();
			}
			return (rev_result);
		} else {
			throw new GenyrisException("reverse: not a list: " + list);
		}
	}

}
