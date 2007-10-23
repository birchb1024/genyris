package org.genyris.interp.builtin;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;
import org.genyris.interp.UnboundException;

public class TagFunction extends ApplicableFunction {
	
	
	public TagFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment environment) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Too few arguments to tag: " + arguments.length);
		Exp object = arguments[0];
		Lobject klass = (Lobject) arguments[1]; //TODO type check
        // call validator if it exists
		// TODO DRY - repeateed code from exp:applyfunction() !
        validateClassTagging(environment, object, klass);
		object.addClass(klass);
		return object;
	}

	public static void validateClassTagging(Environment environment, Exp object, Lobject klass) throws LispinException {
		Exp NIL = environment.getNil();
		Exp validator = null;
        try {
        	validator = klass.lookupVariableValue(environment.getInterpreter().getSymbolTable().internString(Constants.VALIDATE)); // TODO performance
        }
    	catch (UnboundException ignore) {     // TODO would be nice to have a boundp()           	
    	}
    	if( validator != null ) {          		
        	Exp args[] = new Exp[1];
        	args[0] = object;
        	Exp result = validator.applyFunction(environment, args);
        	if( result == NIL) {
        		throw new LispinException("class validator error for object " + object);
        	}
        }
	}

}
