// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StandardClass;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class DefineClassFunction extends ApplicableFunction {
	
    public DefineClassFunction(Interpreter interp) {
    	super(interp, "class", false);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {

		checkMinArguments(arguments, 1);
		Class[] types = {Symbol.class};
		checkArgumentTypes(types, arguments);
        Symbol klassname = (Symbol)arguments[0];
        Exp superklasses = ( arguments.length > 1 ? arguments[1] : NIL);
        StandardClass newClass = StandardClass.makeClass(env, klassname, superklasses);
        if (arguments.length > 2) {
            Exp body = arrayToList(arguments);
            body = body.cdr().cdr();
            body = new Pair(klassname, body);
            body.eval(env);
        }
        return newClass;
    }

}
