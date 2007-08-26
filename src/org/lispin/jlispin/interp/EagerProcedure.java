package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.Visitor;

public class EagerProcedure extends AbstractClosure  {
	// I DO evaluate my arguments before being applied.

	public EagerProcedure(Environment environment, Exp expression, ApplicableFunction appl) throws LispinException {
		super( environment,  expression,  appl);
	}
		
	public Exp[] computeArguments(Environment env, Exp exp) throws LispinException {
		Lsymbol NIL = env.getNil();
		int i = 0;
		Exp[] result = new Exp[exp.length(NIL)];
		while( exp != NIL) {
			result[i] = Evaluator.eval(env, exp.car());
			exp = exp.cdr();
			i++;
		}
		return result;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitEagerProc(this);
	}

	public String toString() {
		return "<EagerProc: " + getJavaValue().toString() + ">";
	}

}
