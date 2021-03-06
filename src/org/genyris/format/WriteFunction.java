// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class WriteFunction extends AbstractFormatFunction {

   public WriteFunction(Interpreter interp) {
        super(interp, "write", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {

        Writer output = new PrintWriter(System.out);
        Formatter formatter = new BasicFormatter(output);
        for (int i=0; i< arguments.length; i++) {
            arguments[i].acceptVisitor(formatter);
            try {
                output.flush();
            } catch (IOException e) {
                 throw new GenyrisException( e.getMessage());
            }
        }
        return TRUE;
    }

}
