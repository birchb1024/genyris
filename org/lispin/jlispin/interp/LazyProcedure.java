

package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Visitor;

public class LazyProcedure extends AbstractClosure {
	// I DO NOT evaluate my arguments before being applied.

	public LazyProcedure(Environment environment, Exp expression, ApplicableFunction appl) throws LispinException {
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

	public void acceptVisitor(Visitor guest) {
		guest.visitLazyProc(this);
	}


}