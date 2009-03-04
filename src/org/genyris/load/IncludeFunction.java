// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load;

import java.io.Writer;

import org.genyris.core.Exp;
import org.genyris.core.Lstring;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.NullWriter;

public class IncludeFunction extends ApplicableFunction {

	public static String getStaticName() {return "include";};
	public static boolean isEager() {return true;};
	
    public IncludeFunction(Interpreter interp) {
    	super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        Exp result;
        Writer out = new NullWriter();
        if( !( arguments[0] instanceof Lstring) ) {
            throw new GenyrisException("non-string argument passed to include: " + arguments[0].toString());
        }
        if( arguments.length > 1 ) {
            if( arguments[1] == TRUE) {
                out = _interp.getDefaultOutputWriter();
            }
        }
        result = SourceLoader.loadScriptFromFile(_interp, arguments[0].toString(), out);

        return result;
    }
}
