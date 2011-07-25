// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
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
                result[i] = expArray[i].toString();
        }
        return result;
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        String[] args = toStringArray(arguments);
        Process child;
        Exp lines = NIL;
        Exp errors = NIL;
        try {
            child = Runtime.getRuntime().exec(args);
            lines = convertResultToListOfLines(child.getInputStream());
            errors = convertResultToListOfLines(child.getErrorStream());
        } catch (IOException e) {
            throw new GenyrisException("exec failed, message is: "
                    + e.getMessage());
        }
        try {
            if (child.waitFor() != 0) {
                throw new GenyrisException(new Pair(lines, errors));
            }
        } catch (InterruptedException e) {
            throw new GenyrisException("exec failed, message is: "
                    + e.getMessage());
        }
        return new Pair(lines, errors);

    }

	private Exp convertResultToListOfLines(InputStream inputStream) throws GenyrisException {
        InputStreamReader read = null;
        BufferedReader buf = null;
        read = new InputStreamReader(inputStream);
        buf = new BufferedReader(read);
        Exp lines = NIL;
		Exp tail = NIL;

		String line;
		try {
			while ((line = buf.readLine()) != null) {
			    if (lines == NIL) {
			        tail = lines = new Pair(new StrinG(line), NIL);
			    } else {
			        tail.setCdr(new Pair(new StrinG(line), NIL));
			        tail = tail.cdr();
			    }
			}
		} catch (AccessException e) {
            throw new GenyrisException("exec failed, " + e.getMessage());
		} catch (IOException e) {
            throw new GenyrisException("exec failed, " + e.getMessage());
		}
    	if( inputStream != null) try {
    		inputStream.close();
    	} catch (IOException ignore) { }
    	if( read != null) try {
				read.close();
    	} catch (IOException ignore) { }
    	if( buf != null) try {
    		buf.close();
    	} catch (IOException ignore) { }
        lines.addClass(ListOfLinesClazz);
		return lines;
	}

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.OS, new ExecMethod(interpreter));
    }
}
