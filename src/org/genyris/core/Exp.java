// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

//
// Exp is short for 'Expression'
//
public abstract class Exp implements Classifiable, Closure {

    public abstract void acceptVisitor(Visitor guest) throws GenyrisException;

    public Exp[] computeArguments(Environment ignored, Exp exp)
            throws GenyrisException {
        Exp[] args = { exp };
        return args;
    }

    public Exp evalCatchOverFlow(Environment env)
            throws GenyrisException {
        try {
            return this.eval(env);
        } catch (StackOverflowError e) {
            throw new GenyrisException("Stack Overflow");
        }
    }

    public abstract Exp eval(Environment env) throws GenyrisException;

    public Exp evalSequence(Environment env) throws GenyrisException {
        throw new GenyrisException("Callto abstract evalSequence.");
    }

    public Exp applyFunction(Environment environment, Exp[] arguments)
            throws GenyrisException {
        if (arguments[0].isNil()) {
            return this;
        }
        Environment newEnv = this.makeEnvironment(environment);
        if (arguments[0].isPair()) {
            return arguments[0].evalSequence(newEnv);
        } else {
            throw new GenyrisException("Arguments to " + this
                    + " must be a list.");
        }
    }

    public abstract Environment makeEnvironment(Environment parent)
            throws GenyrisException;

    public boolean isNil() {
        return false;
    }

    public abstract Exp car() throws AccessException;

    public abstract Exp cdr() throws AccessException;

    public abstract Exp setCar(Exp exp) throws AccessException;

    public abstract Exp setCdr(Exp exp) throws AccessException;

    public abstract boolean isPair();

    public abstract int length(Symbol NIL) throws AccessException;

    public abstract Exp nth(int number, Symbol NIL) throws AccessException;

    public abstract String toString();

    public static void assertIsSymbol(Exp predicate, String message)
            throws GenyrisException {
        if (!(predicate instanceof SimpleSymbol)) {
            throw new GenyrisException(message + predicate);
        }
    }

    public Exp dir(Internable table) {
        return Pair.cons3(new DynamicSymbol(table.SELF()), new DynamicSymbol(
                table.VARS()), new DynamicSymbol(table.CLASSES()), table.NIL());
    }
    public Exp getBody(Exp nil) {
        return nil;
    }
    @Override
    public Exp getPrintableFrame(Exp NIL) {
        Exp body = getBody(NIL);
        Exp location = NIL;
        if (body instanceof PairSource) {
            PairSource source = (PairSource) body;
            location = Pair.cons2(new Bignum(source.lineNumber), new StrinG(
                    source.filename), NIL);
        }
        Exp frame = Pair.cons(new StrinG(toString()), location);
        return frame;
    }
    public boolean isBiscuit()
    {
        return false;
    }


}
