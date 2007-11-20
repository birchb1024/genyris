// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
//
// Warning
//  This code is a playful spike. Not for use.
package org.genyris.java;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;

public class JavaClassForName extends ApplicableFunction {

    public JavaClassForName(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length != 1)
            throw new GenyrisException("Too many or few arguments to java-class: " + arguments.length);

        try {
            return new JavaWrapper(envForBindOperations, (Object) Class.forName( arguments[0].toString()) );
        } catch (ClassNotFoundException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

}
