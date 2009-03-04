// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class Evaluator {

    // TODO - refactor this to use visitor in Exp and gt rid of if()s

    public static Exp eval(Environment env, Exp expression) throws UnboundException,
            AccessException, GenyrisException {
        if (expression.isSelfEvaluating()) {
            return expression;
        }
        else if (expression.getClass() == SimpleSymbol.class) {
            return env.lookupVariableValue(expression);
        }
        else if (expression.listp()) {
            Exp tmp = eval(env, expression.car());
            if(!(tmp instanceof Closure)) {
                throw new GenyrisException("Attempt to call something which is not a Closure: "
                        + expression.toString());
            }
            Closure proc = (Closure) tmp;
            Exp[] arguments = proc.computeArguments(env, expression.cdr());
            return proc.applyFunction(env, arguments);
        }
        else {
            throw new GenyrisException("Evaluator does not know how to eval: "
                    + expression.toString());
        }
    }

    public static Exp evalSequence(Environment env, Exp body) throws GenyrisException {
        SimpleSymbol NIL = env.getNil();
        if (body == NIL) {
            return NIL;
        }
        if (body.cdr() == NIL) {
            return eval(env, body.car());
        }
        else {
            eval(env, body.car());
            return evalSequence(env, body.cdr());
        }
    }

}