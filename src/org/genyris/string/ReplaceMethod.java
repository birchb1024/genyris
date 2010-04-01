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

public class ReplaceMethod extends AbstractStringMethod {
	
	public ReplaceMethod(Interpreter interp) {
        super(interp, "replace");
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
    	Class[] types = {StrinG.class, StrinG.class};
    	this.checkArgumentTypes(types, arguments);
		if(arguments.length > 0) {
			if(!(arguments[0] instanceof StrinG)) {
				throw new GenyrisException("Non string passed to " + Constants.SPLIT);
			}		
		}
		StrinG regex = (StrinG) arguments[0];			
		StrinG replacement = (StrinG) arguments[1];			
		StrinG theString = getSelfString(env);
		return theString.replace(regex, replacement);
	}
}
