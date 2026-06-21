// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ToIntsMethod extends AbstractStringMethod {

	public static String getStaticName() {return Constants.TOINTS;};
	
	public ToIntsMethod(Interpreter interp) {
        super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		Class[] types = { StrinG.class };
		checkArgumentTypes(types, arguments);
		StrinG theString = getSelfString(env);
		return theString.toInts(NIL, arguments[0].toString());
	}
}
