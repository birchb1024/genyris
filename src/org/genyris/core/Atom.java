package org.genyris.core;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.ExpressionEnvironment;

public abstract class Atom extends ExpWithEmbeddedClasses {

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
		throw new AccessException("attempt to take nth of atom: "
				+ this.toString());
	}

	public Exp car() throws AccessException {
		throw new AccessException("attempt to take car of non-pair: "
				+ this.toString());
	}

	public Exp cdr() throws AccessException {
		throw new AccessException("attempt to take cdr of non-pair: "
				+ this.toString());
	}

	public Exp setCar(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}

	public Exp setCdr(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}

	public Environment  makeEnvironment(Environment parent) throws GenyrisException {
		return new ExpressionEnvironment(parent, this);
	}
}
