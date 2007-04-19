package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;

public abstract class Procedure extends Exp {
	
	Environment _env;
	Exp _lambdaExpression;
	final ApplicableFunction _functionToApply;

	public Procedure(Environment environment, Exp expression, ApplicableFunction appl) {
		_env = environment;
		_lambdaExpression = expression;
		_functionToApply = appl;
	}
	
	public Exp getArgument(int index) throws AccessException {
		if( getNumberOfRequiredArguments() <= index)
			return SymbolTable.NIL; // ignore extra arguments 
		else
			return _lambdaExpression.cdr().car().nth(index);
	}

	public Object getJavaValue() {
		return "<" + _functionToApply.getName() + ">";
	}

	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}
	
	public abstract Exp[] computeArguments(Environment env, Exp exp) throws LispinException;

	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		return _functionToApply.bindAndExecute(this, arguments, environment); // double dispatch
	}

	public Environment getEnv() {
		return _env;
	}

	public int getNumberOfRequiredArguments() throws AccessException {
		return _lambdaExpression.cdr().car().length();
	}

	public String getName() {
		return _functionToApply.getName();
	}
	
}