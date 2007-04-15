

package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;

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
		result[i] = exp.car();
		while( (exp = exp.cdr()) != SymbolTable.NIL) {
			i++;
			result[i] = exp.car();
		}
		return result;
	}


}