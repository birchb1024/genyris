// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load;

import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.NullWriter;

public class IncludeFunction extends ApplicableFunction {

    public IncludeFunction(Interpreter interp) {
        super(interp, Constants.PREFIX_SYSTEM + "include", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {
        Exp result;
        Writer out = new NullWriter();
        this.checkArguments(arguments, 1, 2);
        if (!(arguments[0] instanceof StrinG)) {
            throw new GenyrisException(
                    "non-string argument passed to include: "
                            + arguments[0].toString());
        }
        if (arguments.length > 1) {
            if (arguments[1] == TRUE) {
                out = _interp.getDefaultOutputWriter();
            }
        }
        String filename = arguments[0].toString();
        result = SourceLoader.loadScriptFromFile(_interp.getGlobalEnv(),
                _interp.getSymbolTable(), filename, out);

        return result;
    }
}
