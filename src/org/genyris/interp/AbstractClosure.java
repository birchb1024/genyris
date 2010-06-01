// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Atom;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.StandardClass;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public abstract class AbstractClosure extends Atom implements Closure {

	final Environment _env;
	final Exp _lambdaExpression;
	final ApplicableFunction _functionToApply;
	protected int _numberOfRequiredArguments;
	StandardClass _returnClass;

	public AbstractClosure(Environment environment, Exp expression,
			ApplicableFunction appl) {
		_env = environment;
		_lambdaExpression = expression;
		_functionToApply = appl;
		_numberOfRequiredArguments = -1;
		_returnClass = null;
	}

	private Symbol REST() {
		return _env.getSymbolTable().REST();
	}

	private Symbol NIL() {
		return _env.getNil();
	}

	private int countFormalArguments(Exp exp) throws AccessException {
		int count = 0;
		while (exp != NIL()) {
			if (!(exp instanceof Pair)) { // ignore trailing type
											// specification
				break;
			}
			if (((Pair) exp).car() == REST()) {
				// count += 1;
				break;
			}
			count += 1;
			exp = exp.cdr();
		}
		return count;
	}

	public Exp getArgumentOrNIL(int index) throws GenyrisException {
		try {
			return _lambdaExpression.cdr().car().nth(index, NIL());
		} catch (AccessException e) {
			throw new GenyrisException("Additional argument to function "
					+ _lambdaExpression);
		}
	}

	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}

	public Exp getCode() throws AccessException {
		return _lambdaExpression;
	}

	public abstract Exp[] computeArguments(Environment env, Exp exp)
			throws GenyrisException;

	public Exp applyFunction(Environment environment, Exp[] arguments)
			throws GenyrisException {
		
		return _functionToApply.bindAndExecuteAux(this, arguments, environment); // double
		// dispatch
	}

	public Environment getEnv() {
		return _env;
	}

	public int getNumberOfRequiredArguments() throws AccessException {
		if (_numberOfRequiredArguments < 0) {
			_numberOfRequiredArguments = countFormalArguments(_lambdaExpression
					.cdr().car());
		}
		return _numberOfRequiredArguments;
	}

	public String getName() {
		return _functionToApply.getName();
	}

	public Exp lastArgument(Exp args) throws AccessException {
		if (args == NIL())
			return NIL();
		Exp tmp = args;
		while (tmp.cdr() != NIL()) {
			if (!(tmp.cdr() instanceof Pair)) {
				break;
			}
			tmp = tmp.cdr();
		}
		return tmp.car();
	}

	public Exp getLastArgumentOrNIL() throws AccessException {
		Exp args = _lambdaExpression.cdr().car();
		return lastArgument(args);
	}

	public StandardClass getReturnClassOrNull() throws GenyrisException {
		if (_returnClass != null) {
			return _returnClass;
		}
		Exp args = _lambdaExpression.cdr().car();
		Symbol returnTypeSymbolFound = NIL();
		Exp possibleReturnClass = NIL();
		if (args != NIL()) {
			Exp tmp = args;
			while (tmp.cdr() != NIL()) { // TODO refactor this loop into
				if (tmp.cdr() instanceof Symbol) {
					returnTypeSymbolFound = (Symbol)tmp.cdr();
					possibleReturnClass = _env.lookupVariableValue(returnTypeSymbolFound);
					break;
				}
				tmp = tmp.cdr();
			}
		}
		if(possibleReturnClass == NIL()) {
			return null;			
		}
		if(possibleReturnClass instanceof StandardClass) {
			return (_returnClass = (StandardClass)possibleReturnClass);			
		}
		throw new GenyrisException(possibleReturnClass + " return class not a class.");
	}
	public Environment makeEnvironment(Environment parent) throws GenyrisException {
		return new ProcEnvironment(parent, this);
	}
	public Exp dir(Internable table) {
		return Pair.cons2(table.SOURCE(), 
				table.NAME(), 
				Pair.cons3(new DynamicSymbol(table.SELF()), new DynamicSymbol(table.VARS()), new DynamicSymbol(table.CLASSES()), table.NIL()));
	}

}