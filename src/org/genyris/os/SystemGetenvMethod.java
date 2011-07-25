// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os;

import java.util.Iterator;
import java.util.Map;

import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class SystemGetenvMethod extends AbstractMethod {

    public SystemGetenvMethod(Interpreter interp) throws GenyrisException {
        super(interp, "getenv");
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
    	if( arguments.length == 1) {
	    	String key = arguments[0].toString();
	        String value = System.getenv(key);
	        if (value != null) {
	            return new StrinG(value);
	        } else {
	            return NIL;
	        }
    	} else {
    		Map environ = System.getenv();
    		Iterator iter = environ.keySet().iterator();
    		Dictionary result = new Dictionary(env);
            while (iter.hasNext()) {
            	String key = (String)iter.next();
            	String value = (String)environ.get((Object)key);
                result.defineDynamicVariable(new DynamicSymbol((SimpleSymbol) env.internString(key)), (Exp)new StrinG(value));
            }
            return result;

    	}
    }


    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.OS, new SystemGetenvMethod(interpreter));
    }
}
