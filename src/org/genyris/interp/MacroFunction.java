// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;

public class MacroFunction extends ClassicFunction {


    public MacroFunction(String name, Interpreter interp) {
        super(name,interp);
    }

    public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment env) throws GenyrisException  {
        AbstractClosure proc = (AbstractClosure)closure; // TODO run time validation
        return Evaluator.eval(env, super.bindAndExecute( proc, arguments, proc.getEnv()));
    }

}
