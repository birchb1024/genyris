package org.genyris.core;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.ExpressionEnvironment;

public abstract class Atom extends ExpWithEmbeddedClasses implements Comparable {

	public abstract void acceptVisitor(Visitor guest) throws GenyrisException;
	public abstract Exp eval(Environment env) throws GenyrisException;

	public abstract String toString();

	public abstract Symbol getBuiltinClassSymbol(Internable table);

	public boolean isPair() {
		return false;
	}
	public int length(Symbol NIL) throws AccessException {
		throw new AccessException("attempt to take length of atom: "
				+ this.toString());
	}

	public Exp nth(int number, Symbol NIL) throws AccessException {
		throw new AccessException("attempt to take nth " + number + " of atom: "
				+ this.toString());
	}

	public Exp car() throws AccessException {
		throw new AccessException("attempt to take left of non-pair: "
				+ this.toString());
	}

	public Exp cdr() throws AccessException {
		throw new AccessException("attempt to take right of non-pair: "
				+ this.toString());
	}

	public Exp setCar(Exp exp) throws AccessException {
		throw new AccessException("attempt to set left of non-pair");
	}

	public Exp setCdr(Exp exp) throws AccessException {
		throw new AccessException("attempt to set left of non-pair");
	}

	public Environment  makeEnvironment(Environment parent) throws GenyrisException {
		return new ExpressionEnvironment(parent, this);
	}

	public int compareTo(Object other) {
		if(this.getClass() != other.getClass()) {
			return -1;
		} else {
			return this.toString().compareTo(((Atom) other).toString());
		}
	}
}
