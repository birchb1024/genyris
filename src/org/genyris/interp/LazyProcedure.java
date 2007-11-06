// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//


package org.genyris.interp;

import org.genyris.core.AccessException;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;

public class LazyProcedure extends AbstractClosure {
    // I DO NOT evaluate my arguments before being applied.

    public LazyProcedure(Environment env, Exp expression, ApplicableFunction appl) throws GenyrisException {
        super( env,  expression,  appl);
        Exp lazyProcSymbol = env.internString(Constants.LAZYPROCEDURE);
        Exp lazyProcClass = env.lookupVariableValue(lazyProcSymbol);
        addClass(lazyProcClass);
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
        return "<LazyProcedure: " + getJavaValue().toString() + ">";
    }

}