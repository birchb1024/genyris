// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.math.BigDecimal;

import org.genyris.core.Exp;
import org.genyris.core.Lstring;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class KillHTTPDFunction extends ApplicableFunction {
	
	public static String getStaticName() {return "web.kill";};
	public static boolean isEager() {return true;};
	
	public KillHTTPDFunction(Interpreter interp) {
		super(interp);
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
		if (arguments.length != 1)
			throw new GenyrisException("Too many or few arguments to " + getStaticName()
					+ arguments.length);
		// TODO: unsafe downcast
		long id = ((BigDecimal)arguments[0].getJavaValue()).longValue();
		Thread t = getThreadById(id);
		if(t == null) {
			return NIL;
		}
		String name = t.getName();
		t.interrupt();
		return new Lstring("Interrupted " + name);

	}

}
