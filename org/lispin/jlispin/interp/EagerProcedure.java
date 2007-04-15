package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;

public class EagerProcedure extends Exp implements Procedure  {
	// I evaluate my arguments before being applied.
	
	Environment _env;
	Exp _lambdaExpression;
	final Applicable _howToApply;

	public EagerProcedure(Environment environment, Exp expression, Applicable appl) {
		_env = environment;
		_lambdaExpression = expression;
		_howToApply = appl;
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
	
	public Exp[] computeArguments(Environment env, Exp exp) throws Exception {
		int i = 0;
		Exp[] result = new Exp[exp.length()];
		result[i] = env.eval(exp.car());
		while( (exp = exp.cdr()) != SymbolTable.NIL) {
			i++;
			result[i] = env.eval(exp.car());
		}
		return result;
	}
	
	public Applicable getApplyStyle() {
		return _howToApply;
	}

}
