// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.system;

import java.util.Date;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class SystemTicksMethod extends AbstractMethod {

    public SystemTicksMethod(Interpreter interp) throws GenyrisException {
        super(interp, "ticks");
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
    		Date now = new Date(); 
            return new Bignum(now.getTime()); //Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object.
    }


    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.SYSTEM, new SystemTicksMethod(interpreter));
    }
}
