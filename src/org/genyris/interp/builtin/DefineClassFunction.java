// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;

public class DefineClassFunction extends ApplicableFunction {

    public DefineClassFunction(Interpreter interp, Lsymbol name) {
        super(interp, name);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {

        if( arguments.length < 1)
            throw new GenyrisException("Incorrect number of arguments to class.");
        if(! (arguments[0] instanceof Lsymbol)) {
            throw new GenyrisException("class expects a symbols.");
        }
        Exp klassname = arguments[0];
        Exp superklasses = ( arguments.length > 1 ? arguments[1] : NIL);
        Exp newClass = ClassWrapper.makeClass(env, klassname, superklasses);
        if (arguments.length > 2) {
            Exp body = arrayToList(arguments);
            body = body.cdr().cdr();
            body = new Lcons(klassname, body);
            return Evaluator.eval(env, body);
        }
        return newClass;
    }

    public Object getJavaValue() {
        return "<the class builtin function>";
    }

}
