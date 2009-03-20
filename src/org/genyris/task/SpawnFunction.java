// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.exception.GenyrisInterruptedException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class SpawnFunction extends ApplicableFunction {

	public SpawnFunction(Interpreter interp) {
		super(interp, "spawn", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		BackGroundInterpreter task = new BackGroundInterpreter(arrayToStringArray(arguments));
		Thread thread = new Thread(task);
		thread.setName(arguments[0].toString());
		thread.start();

        return new Bignum(thread.getId());
	}
	

    public static class BackGroundInterpreter implements Runnable {
    	private final String[] arguments;
    	public BackGroundInterpreter(String[] args) {
    		arguments = args;
    	}
        public void run() {
        	String filename = arguments[0].toString();
            Interpreter interpreter;
			try {
				interpreter = new Interpreter();
				interpreter.init(false);
				Writer output = new PrintWriter(System.out);
				Exp ARGS = interpreter.intern(Constants.GENYRIS + "system#" + Constants.ARGS);
				interpreter.getGlobalEnv().defineVariable(ARGS, arrayToExpList(interpreter.NIL, arguments));
				SourceLoader.loadScriptFromFile(interpreter, filename, output);
			} catch (GenyrisException e) {
				if(e instanceof GenyrisInterruptedException) 
					return;
				e.printStackTrace();
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
	public static Exp arrayToExpList(Exp NIL, String[] array) {
		Exp result = NIL;
		for (int i = array.length-1; i >= 0; i--) {
            result = new Pair(new StrinG(array[i]), result);
        }
        return result;
    }

}
