

package org.lispin.jlispin.interp;

import org.genyris.core.AccessException;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;

public class LazyProcedure extends AbstractClosure {
	// I DO NOT evaluate my arguments before being applied.

	public LazyProcedure(Environment environment, Exp expression, ApplicableFunction appl) throws LispinException {
		super( environment,  expression,  appl);
	}
	
	public Exp[] computeArguments(Environment env, Exp exp) throws AccessException {
		return makeExpArrayFromList(exp, env.getNil());
	}

	private Exp[] makeExpArrayFromList(Exp exp, Lsymbol NIL) throws AccessException {
		int i = 0;
		Exp[] result = new Exp[exp.length(NIL)];
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

	public String toString() {
		return "<LazyProc: " + getJavaValue().toString() + ">";
	}

}