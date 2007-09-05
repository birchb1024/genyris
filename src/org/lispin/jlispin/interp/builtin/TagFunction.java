package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Constants;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.UnboundException;

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
