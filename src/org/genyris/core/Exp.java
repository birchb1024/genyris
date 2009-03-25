// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.MagicEnvironment;
import org.genyris.interp.builtin.TagFunction;

public abstract class Exp implements Classifiable, Closure {

	public abstract void acceptVisitor(Visitor guest) throws GenyrisException;

	public Exp[] computeArguments(Environment ignored, Exp exp)
			throws GenyrisException {
		Exp[] args = { exp };
		return args;
	}

	public abstract Exp eval(Environment env) throws GenyrisException;


	public Exp applyFunction(Environment environment, Exp[] arguments)
			throws GenyrisException {
		if (arguments[0].isNil()) {
			return this;
		}
		Environment newEnv = new MagicEnvironment(environment, this);
		if (arguments[0].isPair()) {
			return arguments[0].evalSequence(newEnv);
		} else {
			try {
				Dictionary klass = (Dictionary) arguments[0].eval(newEnv);
				// call validator if it exists
				TagFunction.validateObjectInClass(environment, this, klass);
				return this;
			} catch (ClassCastException e) {
				throw new GenyrisException("type tag failure: " + arguments[0]
						+ " is not a class");
			}
		}
	}

	public boolean isNil() {
		return false;
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

	public abstract String toString();

	public boolean isPair() {
		return false;
	}

	public int length(Symbol NIL) throws AccessException {
		return ((Pair)this).length(NIL);
	}

	public Exp nth(int number, Symbol NIL) throws AccessException {
		if (this == NIL) {
			throw new AccessException("nth called on nil.");
		}
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

	public Exp evalSequence(Environment env) throws GenyrisException {

		throw new GenyrisException("Callto abstract evalSequence.");

	}

}
