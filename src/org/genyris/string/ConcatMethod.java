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

public class ConcatMethod extends AbstractStringMethod {

	 
	public static String getStaticName() {return Constants.CONCAT;};

    public ConcatMethod(Interpreter interp) {
        super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        StrinG result = getSelfString(env);
        for(int i=0;i<arguments.length; i++) {
            if(!(arguments[i] instanceof StrinG)) {
                throw new GenyrisException("Non-string passed to " + Constants.CONCAT + ": " + arguments[i].toString());
            }
            result = result.concat((StrinG)arguments[i]);
        }
        return result;
    }
}
