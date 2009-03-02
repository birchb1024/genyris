// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class LengthFunction extends ApplicableFunction {

	public static String getStaticName() {return "length";};
	public static boolean isEager() {return true;};
	
    public LengthFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length != 1)
            throw new GenyrisException("Wrong number of arguments to length function: " + arguments.length);
            int counter = 0;
            Exp s = arguments[0];

            while( s != NIL ) {
                    counter += 1;
                    s = s.cdr();
            }
            return(new Bignum(counter));

    }

}
