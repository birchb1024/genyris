// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class InternFunction extends ApplicableFunction {

	public static String getStaticName() {return "intern";};
	public static boolean isEager() {return true;};
	
    public InternFunction(Interpreter interp) {
        super(interp);
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        if(arguments.length != 1) {
            throw new GenyrisException("Wrong number of arguments to " + getStaticName());
        }
        // TODO - probably a bit too general ? Takes anything!
        return _interp.getSymbolTable().internString(arguments[0].toString());
    }
	public Object getJavaValue() {
        return "[intern builtin function]";
    }

}
