// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.system;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class ExitMethod extends AbstractMethod {

    public ExitMethod(Interpreter interp) throws GenyrisException {
        super(interp, "exit");
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        if( arguments.length < 1 ) {
            System.exit(0); // default ok  exit
            return NIL;
        } else {
            checkArguments(arguments, 1);
            Class[] types = {Bignum.class};
            checkArgumentTypes(types, arguments);
            System.exit(((Bignum)arguments[0]).bigDecimalValue().intValue());
            return NIL;
        }
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.OS, new ExitMethod(interpreter));
    }
}
