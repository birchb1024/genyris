

package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;

public class LazyProcedure extends Procedure {
	// I DO NOT evaluate my arguments before being applied.

	public LazyProcedure(Environment environment, Exp expression, ApplicableFunction appl) {
		super( environment,  expression,  appl);
	}
	
	public Exp[] computeArguments(Environment env, Exp exp) throws AccessException {
		return makeExpArrayFromList(exp);
	}

	private Exp[] makeExpArrayFromList(Exp exp) throws AccessException {
		int i = 0;
		Exp[] result = new Exp[exp.length()];
		while( exp.listp()) {
			result[i] = exp.car();
			exp = exp.cdr();
			i++;
		}
		return result;
	}


}