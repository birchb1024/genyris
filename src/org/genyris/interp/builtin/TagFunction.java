// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.GenyrisTypeMismatchException;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class TagFunction extends ApplicableFunction {


    public TagFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment environment) throws GenyrisException {
        if( arguments.length != 2)
            throw new GenyrisException("Too few arguments to tag: " + arguments.length);
        Exp object = arguments[0];
        Lobject klass = (Lobject) arguments[1]; //TODO type check
        callValidator(environment, object, klass);
        object.addClass(klass);
        return object;
    }



    // TODO move these into ClassWrapper:
    public static void callValidator(Environment environment, Exp object, Lobject klassobject) throws GenyrisException {
        // TODO DRY
        Exp NIL = environment.getNil();
        Exp validator = null;
        ClassWrapper klass = new ClassWrapper(klassobject);
        try {
            validator = klassobject.lookupVariableValue(environment.getInterpreter().getSymbolTable().internString(Constants.VALIDATE)); // TODO performance
        }
        catch (UnboundException ignore) {     // TODO would be nice to have a bound?()
        }
        if( validator != null ) {
            Exp args[] = new Exp[1];
            args[0] = object;
            Exp result = validator.applyFunction(environment, args);
            if( result == NIL) {
                throw new GenyrisException("class " + klass.getClassName() + " validator error for object " + object);
            }
        }
    }
    public static void validateObjectInClass(Environment environment, Exp object, Lobject klassobject) throws GenyrisException {
        Exp NIL = environment.getNil();
        Exp validator = null;
        ClassWrapper klass = new ClassWrapper(klassobject);
        try {
            validator = klassobject.lookupVariableValue(environment.getInterpreter().getSymbolTable().internString(Constants.VALIDATE)); // TODO performance
        }
        catch (UnboundException ignore) {     // TODO would be nice to have a bound?()
            if (!klass.isInstance(object)) {
                throw new GenyrisTypeMismatchException("class " + klass.getClassName() + " validator error for object " + object);
            }
        }
        if( validator != null ) {
            Exp args[] = new Exp[1];
            args[0] = object;
            Exp result = validator.applyFunction(environment, args);
            if( result == NIL) {
                throw new GenyrisException("class " + klass.getClassName() + " validator error for object " + object);
            }
        }
    }

}
