// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ReverseFunction extends ApplicableFunction {

	public static String getStaticName() {return "reverse";};
	public static boolean isEager() {return true;};
	
    public ReverseFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length != 1)
            throw new GenyrisException("Wrong number of arguments to reverse function: " + arguments.length);
            Exp rev_result = NIL;
            Exp s = arguments[0];

            while( s != NIL ) {
                    rev_result = new Lcons(s.car(), rev_result);
                    s = s.cdr();
            }
            return(rev_result);

    }

}
