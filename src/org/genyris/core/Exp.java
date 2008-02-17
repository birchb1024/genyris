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
import org.genyris.interp.Evaluator;
import org.genyris.interp.MagicEnvironment;
import org.genyris.interp.builtin.TagFunction;

public abstract class Exp implements Classifiable, Closure {

    public abstract Object getJavaValue();
    public abstract void acceptVisitor(Visitor guest);

    public Exp[] computeArguments(Environment ignored, Exp exp) throws GenyrisException {
        Exp[] args = {exp};
        return args;
    }

    public Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException {
        if(arguments[0].isNil()) {
            return this;
        }
        Environment newEnv = new MagicEnvironment(environment, this);
        if(arguments[0].listp()) {
            return Evaluator.evalSequence(newEnv, arguments[0]);
        }
        else {
            try {
                Lobject klass = (Lobject) Evaluator.eval(newEnv, arguments[0]);
                // call validator if it exists
                TagFunction.validateObjectInClass(environment, this, klass);
                return this;
            }
            catch (ClassCastException e) {
                throw new GenyrisException("type tag failure: " + arguments[0] + " is not a class");
            }
        }
    }

    public boolean isNil() {
        return false;
    }

    public Exp car() throws AccessException {
        throw new AccessException("attempt to take car of non-pair: " + this.toString() + " ; " + this.getBuiltinClassName());
    }

    public Exp cdr() throws AccessException {
        throw new AccessException("attempt to take cdr of non-pair: " +  this.toString() + " ; " +  this.getBuiltinClassName());
    }

    public Exp setCar(Exp exp) throws AccessException {
        throw new AccessException("attempt to set car of non-cons");
    }

    public Exp setCdr(Exp exp) throws AccessException {
        throw new AccessException("attempt to set car of non-cons");
    }

    public int hashCode() {
        return getJavaValue().hashCode();
    }

    public boolean equals(Object compare) {
        if (compare.getClass() != this.getClass())
            return false;
        else
            return this.getJavaValue().equals(((Exp) compare).getJavaValue());
    }

    public boolean deepEquals(Object compare) {
        if (compare.getClass() != this.getClass())
            return false;
        else
            return this.getJavaValue().equals(((Exp) compare).getJavaValue());
    }


    public abstract String toString();


    public boolean listp() {
        return (this instanceof Lcons);
    }

    public boolean isSelfEvaluating() {
        return true;
    }

    public int length(Lsymbol NIL) throws AccessException {
        Exp tmp = this;
        int count = 0;

        while (tmp != NIL) {
            tmp = tmp.cdr();
            count++;
        }
        return count;
    }

    public Exp nth(int number, Lsymbol NIL) throws AccessException {
        if (this == NIL) {
            throw new AccessException("nth called on nil.");
        }
        Exp tmp = this;
        int count = 0;
        while (tmp != NIL) {
            if (count == number) {
                return tmp.car();
            }
            tmp = tmp.cdr();
            count++;
        }
        throw new AccessException("nth could not find item: " + number);
    }


}
