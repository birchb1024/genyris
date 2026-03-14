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
import java.util.*;

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

    public GenyrisException error(String msg) {
        return new GenyrisException(_name + ": " + msg);
    }

    private String[] toStringArray(Exp args) throws GenyrisException {
        List<String> result = new ArrayList<>();
        while (args != this.NIL ) {
            if(!(args instanceof Pair)) {
                throw error("not a list in arguments " + args);
            }
            result.add(args.car().toString());
            args = args.cdr();
        }
        return result.toArray(new String[0]);
    }
    private String[] toEnvStringArray(Exp args) throws GenyrisException {
        List<String> result = new ArrayList<>();
        while (args != this.NIL ) {
            if(!(args instanceof Pair)) {
                throw error("not a list in arguments " + args);
            }
            if(!(args.car() instanceof Pair)) {
                throw error("not a list in arguments " + args.car());
            }
            result.add(args.car().car().toString() + "=" +  args.car().cdr().toString());
            args = args.cdr();
        }
        return result.toArray(new String[0]);
    }

    private String[] toStringArray(Exp[] expArray) throws GenyrisException {
        String[] result = new String[expArray.length];
        for (int i = 0; i < expArray.length; i++) {
                result[i] = expArray[i].toString();
        }
        return result;
    }

    public enum CallType {
        Error,
        AllStrings,
        ListofStringsNoEnv,
        ListofStringsWithEnv
    }
    public static boolean isAllSameClass(Class klass, Exp[] args) {
        for(int i = 0 ; i < args.length ; i++){
            if (! klass.isInstance(args[i])) {
                return false;
            }
        }
        return true;
    }
    public boolean isListAllSameClass(Class klass, Exp args) throws GenyrisException {
        if (args == this.NIL ) {
            return true;
        }
        if(!(args instanceof Pair)) {
            throw error("not a list in arguments " + args);
        }
        if (! klass.isInstance(args.car())) {
                return false;
        }
        return isListAllSameClass(klass, args.cdr());
    }
    public boolean isListSimpleAssoc(Exp args) throws GenyrisException {

        if (args == this.NIL ) {
            return true;
        }
        if(!(args instanceof Pair)) {
            throw error("not a list in environment " + args);
        }
        if (! (args.car() instanceof Pair)) {
                throw error("not a pair in environment " + args.car());
        }
        if (! (args.car().car() instanceof StrinG)) {
                throw error("not a String in environment " + args.car());
        }
        return isListSimpleAssoc(args.cdr());
    }
    public CallType classifyArguments(Exp[] arguments) throws GenyrisException {
        if( arguments.length == 0) {
            throw error("Insufficient arguments " + arguments);
        }
        if( isAllSameClass(StrinG.class, arguments) ){
            return CallType.AllStrings;
        }
        if( arguments.length == 1 && isAllSameClass(Pair.class, arguments) ){
            if(isListAllSameClass(StrinG.class, arguments[0]) ){
                return CallType.ListofStringsNoEnv;
            }
        }
        if( arguments.length == 2 && arguments[1] == NIL) {
                Exp[] tmp = {arguments[0]};
                return classifyArguments(tmp);
        }
        if( arguments.length == 2 && isAllSameClass(Pair.class, arguments) ){
            if(arguments[1] == NIL) {
                Exp[] tmp = {arguments[0]};
                return classifyArguments(tmp);
            }
            if(isListAllSameClass(StrinG.class, arguments[0]) && isListAllSameClass(Pair.class, arguments[1]) ){
                if(!isListSimpleAssoc(arguments[1])) {
                    throw error("Environment variables not strings " + arguments[1]);
                }
                return CallType.ListofStringsWithEnv;
            }
        }
        throw error("Unknown argument structure " + Arrays.toString(arguments));
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        Process child;
        Exp lines = NIL;
        Exp errors = NIL;
        try {
            switch (classifyArguments(arguments)) {
                case ListofStringsWithEnv: {
                        String[] cmdarray = toStringArray(arguments[0]);
                        String[] envarray = toEnvStringArray(arguments[1]);
                        List<String> envList = updateEnvironment(envarray);
                        child = Runtime.getRuntime().exec(cmdarray, envList.toArray(new String[0]));
                        lines = convertResultToListOfLines(child.getInputStream());
                        errors = convertResultToListOfLines(child.getErrorStream());
                    }
                    break;
                case ListofStringsNoEnv: {
                        String[] cmdarray = toStringArray(arguments[0]);
                        child = Runtime.getRuntime().exec(cmdarray);
                        lines = convertResultToListOfLines(child.getInputStream());
                        errors = convertResultToListOfLines(child.getErrorStream());
                    }
                    break;
                case AllStrings:
                    String[] args = toStringArray(arguments);
                    child = Runtime.getRuntime().exec(args);
                    lines = convertResultToListOfLines(child.getInputStream());
                    errors = convertResultToListOfLines(child.getErrorStream());
                    break;
                default:
                    throw error("Unknown argument structure " + Arrays.toString(arguments));
            }
        } catch (IOException e) {
            throw error("failed: " + e.getMessage());
        }
        try {
            if (child.waitFor() != 0) {
                throw error(new Pair(lines, errors).toString());
            }
        } catch (InterruptedException e) {
            throw error( "failed:"+ e.getMessage().toString());
        }
        return new Pair(lines, errors);

    }

    private static List<String> updateEnvironment(String[] envarray) {
        // BEGIN Eeewww...
        Map<String, String> currentEnv =  new HashMap<>(System.getenv()); // getEnv() returns non-modifiable map, but HashMap is mutable
        // insert or update current variables
        for(int i = 0; i < envarray.length ; i++) {
            String[] toke = envarray[i].split("=");
            if(toke.length == 1 ) {
                currentEnv.put(toke[0], "");
            }
            if(toke.length > 1 ) {
                currentEnv.put(toke[0], toke[1]);
            }
        }
        List<String> envList = asListofKeqV(currentEnv);
        // END Eeewww...
        return envList;
    }

    private static List<String> asListofKeqV(Map<String, String> M) {
        List<String> envList = new ArrayList<>();
        for (Map.Entry<String, String> entry: M.entrySet()) {
            envList.add(entry.getKey() + "=" + entry.getValue());
        }
        return envList;
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
            throw error(" failed, " + e.getMessage());
		} catch (IOException e) {
            throw error(" failed, " + e.getMessage());
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
