// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.*;

public class HaltMethod extends AbstractMethod {

    public HaltMethod(Interpreter interp) throws GenyrisException {
        super(interp, "halt");
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        int status = 0;
        if( arguments.length > 1 ) {
            checkArguments(arguments, 1);
            Class[] types = {Bignum.class};
            checkArgumentTypes(types, arguments);
            status = ((Bignum)arguments[0]).bigDecimalValue().intValue();
            return NIL;
        }
        Runtime.getRuntime().halt(0);
        return NIL;
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.OS, new HaltMethod(interpreter));
    }
}
