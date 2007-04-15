

package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;

public class LazyProcedure extends Exp implements Procedure {
	// I do not evaluate my arguments before being applied.
	
	Environment _env;
	Exp _lambdaExpression;
	final Applicable _howToApply;

	public LazyProcedure(Environment environment, Exp expression, Applicable appl) {
		_howToApply = appl;
		_env = environment;
		_lambdaExpression = expression;
	}
	
	/* (non-Javadoc)
	 * @see org.lispin.jlispin.interp.Procedure#getArgument(int)
	 */
	public Exp getArgument(int i) throws AccessException {
		return _lambdaExpression.cdr().car().nth(i);
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.lispin.jlispin.interp.Procedure#getBody()
	 */
	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}

	public Exp[] computeArguments(Environment env, Exp exp) throws Exception {
		return makeExpArrayFromList(exp);
	}

	private Exp[] makeExpArrayFromList(Exp exp) throws AccessException {
		int i = 0;
		Exp[] result = new Exp[exp.length()];
		result[i] = exp.car();
		while( (exp = exp.cdr()) != SymbolTable.NIL) {
			i++;
			result[i] = exp.car();
		}
		return result;
	}

	public Applicable getApplyStyle() {
		return _howToApply;
	}


}