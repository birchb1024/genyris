// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class MatchMethod extends AbstractStringMethod {

	public MatchMethod(Interpreter interp, Lsymbol name) {
        super(interp, name);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		if(arguments.length > 0) {
			if(!(arguments[0] instanceof Lstring)) {
				throw new GenyrisException("Non string passed to " + Constants.MATCH);
			}
			Lstring regex = (Lstring) arguments[0];			
			Lstring theString = getSelfString(env);
			return theString.match(NIL, TRUE, regex);
		}
		else {
			throw new GenyrisException("Missing argument to " + Constants.MATCH);			
		}
	}
}
