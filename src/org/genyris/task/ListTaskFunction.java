// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ListTaskFunction extends TaskFunction {

    public ListTaskFunction(Interpreter interp) {
        super(interp, "ps", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        Thread myself = Thread.currentThread();
        final Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        Exp threadList = NIL;
        for ( int i=0 ; i < threads.length; i++) {
        	if(threads[i] != null && threads[i].getId() != myself.getId())
                threadList = new Pair(getThreadAsDictionary(threads[i], envForBindOperations), threadList);
        }
        threadList = new Pair(getThreadAsDictionary(myself, envForBindOperations), threadList);
        return threadList;
    }

}
