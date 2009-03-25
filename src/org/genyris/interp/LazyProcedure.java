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
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class LazyProcedure extends AbstractClosure {
    // I DO NOT evaluate my arguments before being applied.

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.LAZYPROC();
	}

    public LazyProcedure(Environment env, Exp expression, ApplicableFunction appl) throws GenyrisException {
        super( env,  expression,  appl);
    }

    public Exp[] computeArguments(Environment env, Exp exp) throws AccessException {
        return makeExpArrayFromList(exp, env.getNil());
    }

    private Exp[] makeExpArrayFromList(Exp exp, Symbol NIL) throws AccessException {
        int i = 0;
        Exp[] result = new Exp[exp.length(NIL)];
        while( exp.listp()) {
            result[i] = exp.car();
            exp = exp.cdr();
            i++;
        }
        return result;
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitLazyProc(this);
    }

    public String toString() {
        return "<LazyProcedure: " + _functionToApply.toString() + ">";
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

}