// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ListTaskFunction extends TaskFunction {

    public ListTaskFunction(Interpreter interp) {
        super(interp, "ps", true);
    }

    private Exp getThreadAsList( Thread tr) {
    	return
    		new Pair(new StrinG(tr.getState().toString()),
    		new Pair(new StrinG(tr.getName()),
    		new Pair(new Bignum(tr.getId()),NIL)));
    }


    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        final Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        Exp threadList = NIL;
        for ( int i=0 ; i < threads.length; i++) {
            threadList = new Pair(getThreadAsList(threads[i]), threadList);
        }
        return threadList;
    }

}
