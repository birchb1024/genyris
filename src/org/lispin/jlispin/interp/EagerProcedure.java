package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.Visitor;
import org.lispin.jlispin.interp.Evaluator;

public class EagerProcedure extends AbstractClosure  {
	// I DO evaluate my arguments before being applied.

	public EagerProcedure(Environment environment, Exp expression, ApplicableFunction appl) throws LispinException {
		super( environment,  expression,  appl);
	}
		
	public Exp[] computeArguments(Environment env, Exp exp) throws LispinException {
		int i = 0;
		Exp[] result = new Exp[exp.length()];
		while( exp != SymbolTable.NIL) {
			result[i] = Evaluator.eval(env, exp.car());
			exp = exp.cdr();
			i++;
		}
		return result;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitEagerProc(this);
	}
	public void addClass(Exp klass) {
		; // Noop
	}

	public Exp getClasses() {
		// TODO Auto-generated method stub
		return SymbolTable.NIL;
	}

	public void removeClass(Exp klass) {
		; // Noop
	}

}
