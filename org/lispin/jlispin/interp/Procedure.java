package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;

public abstract class Procedure extends Exp {
	
	Environment _env;
	Exp _lambdaExpression;
	final ApplicableFunction _howToApply;

	public Procedure(Environment environment, Exp expression, ApplicableFunction appl) {
		_env = environment;
		_lambdaExpression = expression;
		_howToApply = appl;
	}
	
	public Exp getArgument(int i) throws AccessException {
		return _lambdaExpression.cdr().car().nth(i);
	}

	public Object getJavaValue() {
		return "<procedure>";
	}

	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}
	
	public abstract Exp[] computeArguments(Environment env, Exp exp) throws LispinException;

	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		return _howToApply.apply(this, environment, arguments); // double dispatch
	}

	public Environment getEnv() {
		return _env;
	}
	
}