// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.exception.GenyrisInterruptedException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

import static org.genyris.interp.ClassicReadEvalPrintLoop.getContainingDirectoryPath;

public class SpawnFunction extends TaskFunction {

	public SpawnFunction(Interpreter interp) {
		super(interp, "spawn", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkMinArguments(arguments, 1);
		BackGroundInterpreter task = new BackGroundInterpreter(arguments);
		Thread thread = new Thread(task);
		thread.setName(arrayOfExpToString(arguments));
		Exp result = getThreadAsDictionary(thread, envForBindOperations);
		thread.start();
        return result;
	}
	

    public static class BackGroundInterpreter implements Runnable {
    	private final Exp[] arguments;
    	public BackGroundInterpreter(Exp[] args) throws GenyrisException {
    		arguments = args;
    	}
        public void run() {
            Interpreter interpreter;
			try {
	    		if( arguments.length != 0) {
	    			String filename = arguments[0].toString();
					interpreter = new Interpreter();
					interpreter.init(false, getContainingDirectoryPath(filename));
					Writer output = new PrintWriter(System.out);
					Symbol argv = interpreter.intern(new PrefixSymbol(Constants.GENYRIS + "system#", Constants.ARGV, "sys"));
					interpreter.getGlobalEnv().defineVariable(argv, arrayToExpList(interpreter.NIL, arguments));
	    			SourceLoader.loadScriptFromFile(interpreter.getGlobalEnv(), interpreter.getSymbolTable(), filename, output);
	    		}
			} catch (GenyrisException e) {
				if(e instanceof GenyrisInterruptedException) {
					System.out.println("*** GenyrisInterruptedException " + Thread.currentThread().getName() + ' ' + e.getMessage());
					Thread.currentThread().interrupt();
					return;
				}
				System.out.println("*** Error in thread " + Thread.currentThread().getName() + ' ' + e.getMessage());
			}
        
        }

    }

	public static Exp arrayToExpList(Exp NIL, Exp[] array) {
		Exp result = NIL;
		for (int i = array.length-1; i >= 0; i--) {
            result = new Pair(array[i], result);
        }
        return result;
    }

	public static String arrayOfExpToString(Exp[] array) {
		String result = "";
		for (int i = 0; i<array.length; i++) {
            result += (i>0?" ":"") + array[i].toString();
        }
        return result;
    }
}
