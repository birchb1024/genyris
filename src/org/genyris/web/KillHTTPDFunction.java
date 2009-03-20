// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class KillHTTPDFunction extends ApplicableFunction {

    public KillHTTPDFunction(Interpreter interp) {
    	super(interp, Constants.WEB + "kill", true);
    }

    private Thread getThreadById( final long id ) {
        final Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for ( int i=0 ; i < threads.length; i++) {
            if ( threads[i].getId( ) == id )
                return threads[i];
        }
        return null;
    }


    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 1);
    	Class[] types = {Bignum.class};
    	checkArgumentTypes(types, arguments);
        long id = ((Bignum)arguments[0]).bigDecimalValue().longValue();
        Thread t = getThreadById(id);
        if(t == null) {
            return NIL;
        }
        String name = t.getName();
        t.interrupt();
        return new StrinG("Interrupted " + name);

    }

}
