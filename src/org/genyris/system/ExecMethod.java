// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class ExecMethod extends AbstractMethod {

    private Dictionary ListOfLinesClazz;

    public ExecMethod(Interpreter interp) throws GenyrisException {
        super(interp, "exec");
        ListOfLinesClazz = (Dictionary)interp.lookupGlobalFromString(Constants.LISTOFLINES);
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
        String[] args = toStringArray(arguments);
        Process child;
        InputStream childOut = null;
        InputStreamReader read = null;
        BufferedReader buf = null;
        Exp lines = NIL;
        try {
            child = Runtime.getRuntime().exec(args);
            childOut = child.getInputStream();
            read = new InputStreamReader(childOut);
            buf = new BufferedReader(read);
            Exp tail = NIL;

            String line;
            while ((line = buf.readLine()) != null) {
                if (lines == NIL) {
                    tail = lines = new Pair(new StrinG(line), NIL);
                } else {
                    tail.setCdr(new Pair(new StrinG(line), NIL));
                    tail = tail.cdr();
                }
            }
        } catch (IOException e) {
            throw new GenyrisException("exec failed, message is: "
                    + e.getMessage());
        } finally {
        	if( childOut != null) try {
					childOut.close();
			} catch (IOException ignore) { }
	        if( read != null) try {
						read.close();
			} catch (IOException ignore) { }
	        if( buf != null) try {
				buf.close();
	        } catch (IOException ignore) { }
        }
        try {
            if (child.waitFor() != 0) {
              throw new GenyrisException("exec failed, return is: " + new Integer(child.exitValue()).toString());
            }
        } catch (InterruptedException e) {
            throw new GenyrisException("exec failed, message is: "
                    + e.getMessage());
        }
        lines.addClass(ListOfLinesClazz);
        return lines;

    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.SYSTEM, new ExecMethod(interpreter));
    }
}
