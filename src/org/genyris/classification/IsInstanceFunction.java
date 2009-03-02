// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classification;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class IsInstanceFunction extends ApplicableFunction {

	public static String getStaticName() {return "is-instance?";};
	public static boolean isEager() {return true;};

	public IsInstanceFunction(Interpreter interp) {
        super(interp);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
        if( arguments.length < 2)
            throw new GenyrisException("Too few arguments to is-instance?: " + arguments.length);
        if(!(arguments[1] instanceof Lobject) )
            throw new GenyrisException("Wrong argument 2 to is-instance? was expecting a class, not: " + arguments[1]);
        ClassWrapper cw = new ClassWrapper((Lobject)arguments[1]); // TODO check and throw
        if (cw.isInstance(arguments[0]) )
            return envForBindOperations.internString(Constants.TRUE);
        else
            return envForBindOperations.getNil();
    }

}
