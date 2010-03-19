// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.exception.GenyrisInterruptedException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class SpawnFunction extends TaskFunction {

	public SpawnFunction(Interpreter interp) {
		super(interp, "spawn", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		
		BackGroundInterpreter task = new BackGroundInterpreter(arguments);
		Thread thread = new Thread(task);
		if( arguments.length > 0 )
			thread.setName(arguments[0].toString());
		thread.start();

        return getThreadAsDictionary(thread, envForBindOperations);
	}
	

    public static class BackGroundInterpreter implements Runnable {
    	private final Exp[] arguments;
    	public BackGroundInterpreter(Exp[] args) throws GenyrisException {
    		arguments = args;
    	}
        public void run() {
            Interpreter interpreter;
			try {
				interpreter = new Interpreter();
				interpreter.init(false);
				Writer output = new PrintWriter(System.out);
				SimpleSymbol ARGS = interpreter.intern(Constants.GENYRIS + "system#" + Constants.ARGS);
				interpreter.getGlobalEnv().defineVariable(ARGS, arrayToExpList(interpreter.NIL, arguments));
	    		if( arguments.length != 0) {
	    			String filename = arguments[0].toString();
	    			SourceLoader.loadScriptFromFile(interpreter.getGlobalEnv(), interpreter.getSymbolTable(), filename, output);
	    		}
			} catch (GenyrisException e) {
				if(e instanceof GenyrisInterruptedException) 
					return;
				System.out.println("*** Error in thread " + Thread.currentThread().getName() + ' ' + e.getMessage());
			}

        }

    }

	public static String[] arrayToStringArray(Exp[] array) {
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }
	public static Exp arrayToExpList(Exp NIL, Exp[] array) {
		Exp result = NIL;
		for (int i = array.length-1; i >= 0; i--) {
            result = new Pair(array[i], result);
        }
        return result;
    }

}
