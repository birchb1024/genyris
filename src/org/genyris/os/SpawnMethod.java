// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os;

import java.io.IOException;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.java.JavaWrapper;

public class SpawnMethod extends AbstractMethod {


    public SpawnMethod(Interpreter interp) throws GenyrisException {
        super(interp, "spawn");
    }

    private String[] toStringArray(Exp[] expArray) throws GenyrisException {
        String[] result = new String[expArray.length];
        for (int i = 0; i < expArray.length; i++) {
            if (!(expArray[i] instanceof StrinG)) {
              throw new GenyrisException(Constants.EXEC + " Non-string: " + expArray[i]);
            } else {
                result[i] = ((StrinG) expArray[i]).toString();
            }
        }
        return result;
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
    	Process child;
        String[] args = toStringArray(arguments);
        try {
            child = Runtime.getRuntime().exec(args);
        } catch (IOException e) {
            throw new GenyrisException("exec failed, message is: "
                    + e.getMessage());
        }
        return new JavaWrapper(child);

    }


    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.OS, new SpawnMethod(interpreter));
    }
}
