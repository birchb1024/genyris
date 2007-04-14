package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;

public class Procedure extends Exp {
	
	Environment _env;
	Exp _lambdaExpression;

	public Procedure(Environment environment, Exp expression) {
		_env = environment;
		_lambdaExpression = expression;
	}
	
	public Exp getArgument(int i) throws AccessException {
		return _lambdaExpression.cdr().car().nth(i);
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}

}
