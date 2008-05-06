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
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class ExecMethod extends AbstractMethod {
    
    private Exp ListOfLinesClazz; 
    public ExecMethod(Interpreter interp, Lsymbol name) throws GenyrisException {
        super(interp, name);
        ListOfLinesClazz = interp.lookupGlobalFromString(Constants.LISTOFLINES);
    }

    private String[] toStringArray(Exp[] expArray) throws GenyrisException {
        String[] result = new String[expArray.length];
        for (int i = 0; i < expArray.length; i++) {
            if (!(expArray[i] instanceof Lstring)) {
                throw new GenyrisException("Non-string passed to " + Constants.EXEC);
            } else {
                result[i] = ((Lstring)expArray[i]).toString();
            }
        }
        return result;
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
		String []args = toStringArray(arguments);
		Process child;
        Exp lines = NIL;
        try {
            child = Runtime.getRuntime().exec(args);
            InputStream childOut = child.getInputStream();
            InputStreamReader read = new InputStreamReader(childOut);
            BufferedReader buf = new BufferedReader(read);
            Exp tail = NIL;
            
            String line;
            while((line = buf.readLine()) != null) {
                if(lines == NIL) {
                    tail = lines = new Lcons(new Lstring(line), NIL);
                } else {
                    tail.setCdr(new Lcons(new Lstring(line), NIL));
                    tail = tail.cdr();
                }
            }
        }
        catch (IOException e) {
            throw new GenyrisException("exec failed, message is: " + e.getMessage());
        }
        try {
            if(child.waitFor() != 0) {
                lines = new Lcons(new Lstring("exec return value = " + child.exitValue()) , lines);
            }
        }
        catch (InterruptedException e) {
            throw new GenyrisException("exec failed, message is: " + e.getMessage());
        }
        lines.addClass(ListOfLinesClazz);
		return lines;
        
	}
}
