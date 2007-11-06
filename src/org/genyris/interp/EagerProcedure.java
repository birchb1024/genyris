// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.classes.BuiltinClasses;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;

public class EagerProcedure extends AbstractClosure  {
    // I DO evaluate my arguments before being applied.

    public EagerProcedure(Environment environment, Exp expression, ApplicableFunction appl) throws GenyrisException {
        super( environment,  expression,  appl);
        addClass(BuiltinClasses.EAGERPROCEDURE);

    }

    public Exp[] computeArguments(Environment env, Exp exp) throws GenyrisException {
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