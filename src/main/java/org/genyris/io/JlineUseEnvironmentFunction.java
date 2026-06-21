// Copyright 2019 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.*;


public class JlineUseEnvironmentFunction  extends ApplicableFunction {

    public JlineUseEnvironmentFunction(Interpreter interp) {
        super(interp, Constants.PREFIX_SYSTEM + "jline-use-current-environment", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
            this.checkArguments(arguments, 0, 0);
            JlineStdioInStream the_jline = JlineStdioInStream.knew();
            the_jline.setEnvironment(env);
            return NIL;
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new JlineUseEnvironmentFunction(interpreter));
    }

}
