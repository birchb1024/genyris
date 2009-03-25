// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;

public class EagerProcedure extends AbstractClosure  {
    // I DO evaluate my arguments before being applied.

    public EagerProcedure(Environment env, Exp expression, ApplicableFunction appl) throws GenyrisException {
        super( env,  expression,  appl);
    }

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.EAGERPROC();
	}

    public Exp[] computeArguments(Environment env, Exp exp) throws GenyrisException {
        Symbol NIL = env.getNil();
        int i = 0;
        Exp[] result = new Exp[exp.length(NIL)];
        while( exp != NIL) {
            result[i] = Evaluator.eval(env, exp.car());
            exp = exp.cdr();
            i++;
        }
        return result;
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitEagerProc(this);
    }

    public String toString() {
        return "<EagerProc: " + _functionToApply.toString() + ">";
    }

	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

}
