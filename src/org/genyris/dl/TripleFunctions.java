//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class TripleFunctions extends ApplicableFunction {

    public TripleFunctions(Interpreter interp) {
        super(interp, "triple", true);
    }

    public Symbol toSymbol(Exp x) throws GenyrisException {
        if( x instanceof Bignum ||  x instanceof StrinG ||  x instanceof Symbol ) {
            return _interp.intern(x.toString());
        }
        else {
            throw new GenyrisException( "Cannot make a triple with " + x.toString() + " " + x.getClass().getName());
        }
    }
    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        checkArguments(arguments, 3);
        Class[] types = { Exp.class, Exp.class, Exp.class };
        checkArgumentTypes(types, arguments);
        return new Triple(toSymbol(arguments[0]), toSymbol(arguments[1]), arguments[2]);
    }

    public static abstract class AbstractTripleMethod extends AbstractMethod {

        public AbstractTripleMethod(Interpreter interp, String name) {
            super(interp, name);
        }

        protected Triple getSelfAsTriple(Environment env) throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof Triple)) {
                throw new GenyrisException(
                        "Non-Triple passed to a Triple method.");
            } else {
                return (Triple) _self;
            }
        }
    }


    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new TripleFunctions(interpreter));
    }

}
