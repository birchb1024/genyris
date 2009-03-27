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
import org.genyris.interp.builtin.TagFunction;

public abstract class Exp implements Classifiable, Closure {

	public abstract void acceptVisitor(Visitor guest) throws GenyrisException;

	public Exp[] computeArguments(Environment ignored, Exp exp)
			throws GenyrisException {
		Exp[] args = { exp };
		return args;
	}

	public abstract Exp eval(Environment env) throws GenyrisException;
	public Exp evalSequence(Environment env) throws GenyrisException {
		throw new GenyrisException("Callto abstract evalSequence.");
	}



	public Exp applyFunction(Environment environment, Exp[] arguments)
			throws GenyrisException {
		if (arguments[0].isNil()) {
			return this;
		}
		Environment newEnv = this.makeEnvironment(environment);
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

	public abstract Environment  makeEnvironment(Environment parent) throws GenyrisException;

	public boolean isNil() {
		return false;
	}

	public abstract Exp car() throws AccessException;
	public abstract Exp cdr() throws AccessException;
	public abstract Exp setCar(Exp exp) throws AccessException ;
	public abstract Exp setCdr(Exp exp) throws AccessException;
	public abstract boolean isPair();
	public abstract int length(Symbol NIL) throws AccessException;
	public abstract  Exp nth(int number, Symbol NIL) throws AccessException;

	public abstract String toString();



}
