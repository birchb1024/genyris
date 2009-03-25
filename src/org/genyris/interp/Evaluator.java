// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.exception.GenyrisException;

public class Evaluator {

    public static Exp evalSequence(Environment env, Exp body) throws GenyrisException {
        SimpleSymbol NIL = env.getNil();
        if (body == NIL) {
            return NIL;
        }
        if (body.cdr() == NIL) {
            return body.car().eval(env);
        }
        else {
            body.car().eval(env);
            return evalSequence(env, body.cdr());
        }
    }

}