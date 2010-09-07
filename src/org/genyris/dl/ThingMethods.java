//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Pair;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.interp.builtin.ObjectFunction.AbstractDictionaryMethod;

public class ThingMethods {

    public static class AsGraphMethod extends ApplicableFunction {
        public AsGraphMethod(Interpreter interp) {
            super(interp, "asGraph", true);
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            checkArguments(arguments, 0);
            Graph ts = new Graph();
            ExpWithEmbeddedClasses self = (ExpWithEmbeddedClasses) env
                    .getSelf();
            Exp classes = self.getClasses(env);
            // TODO move this code int ExpWithEmbeddedClasses as a method
            while (classes != NIL) {
                ts.add(new Triple(self, env.getSymbolTable().TYPE(), classes
                        .car()));
                classes = classes.cdr();
            }
            return ts;
        }
    }

    public static class AsTriplesMethod extends AbstractDictionaryMethod {

        public AsTriplesMethod(Interpreter interp) {
            super(interp, "asTriples");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            Exp results = NIL;
            ExpWithEmbeddedClasses self = (ExpWithEmbeddedClasses) env
                    .getSelf();
            checkArguments(arguments, 0);
            Exp classes = self.getClasses(env);
            // TODO move this code int ExpWithEmbeddedClasses as a method
            while (classes != NIL) {
                results = new Pair(new Triple(self, env.getSymbolTable()
                        .TYPE(), classes.car()), results);
                classes = classes.cdr();
            }
            return results;
        }
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance("Thing", new AsGraphMethod(interpreter));
        interpreter.bindMethodInstance("Thing", new AsTriplesMethod(interpreter));
    }

}
