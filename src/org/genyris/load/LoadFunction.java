// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load;

import java.io.IOException;
import java.io.Writer;

import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.NullWriter;

public class LoadFunction extends ApplicableFunction {

    public LoadFunction(Interpreter interp) {
        super(interp, "load", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
        Exp result = NIL;
        checkMinArguments(arguments, 1);
        try {
            if( !( arguments[0] instanceof StrinG) ) {
                throw new GenyrisException("non-string file path argument passed to load: " + arguments[0].toString());
            }
            if( arguments.length > 1 ) {
                if( arguments[1] == TRUE) {
                    Writer out = _interp.getDefaultOutputWriter();
                    result = SourceLoader.loadScriptFromClasspath(_interp.getGlobalEnv(), 
                            _interp.getSymbolTable(), arguments[0].toString(), out);
                    return result;
                }
            }
        
            Writer out = new NullWriter();
            result = SourceLoader.loadScriptFromClasspath(_interp.getGlobalEnv(), 
                    _interp.getSymbolTable(), arguments[0].toString(), out);
            out.close();
        } catch (IOException unknown) {
            throw new GenyrisException(unknown.getMessage());
        }
        return result;
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws GenyrisException {
        interpreter.bindGlobalProcedureInstance(new LoadFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new IncludeFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new ImportFunction(interpreter));
    }
}
