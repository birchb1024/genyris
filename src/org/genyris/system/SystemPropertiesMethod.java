// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.system;

import java.util.Enumeration;
import java.util.Properties;

import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.EscapedSymbol;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class SystemPropertiesMethod extends AbstractMethod {

    public SystemPropertiesMethod(Interpreter interp) throws GenyrisException {
        super(interp, "getProperties");
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
    	return convertPropertiesToDictionary(env, System.getProperties());
    }

	public Dictionary convertPropertiesToDictionary(Environment env, Properties props) throws GenyrisException {
		Dictionary retval = new Dictionary(env);
    	Enumeration iter = props.propertyNames();
    	while(iter.hasMoreElements()) {
    		String key = (String)iter.nextElement();
    		retval.defineVariableRaw(env.getSymbolTable().internSymbol(new EscapedSymbol(key)), new StrinG(props.getProperty(key)));
    	}
    	return retval;
	}

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.SYSTEM, new SystemPropertiesMethod(interpreter));
    }
}
