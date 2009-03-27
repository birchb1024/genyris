// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.Dictionary;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.builtin.TagFunction;

public class ClassicFunction extends ApplicableFunction {
    public ClassicFunction(String name, Interpreter interp) {
        super(interp, name, true);
    }
    public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
        // TODO clean it up what a right royal mess
    	if(!(closure instanceof AbstractClosure)) {
    		throw new GenyrisException("type missmatch - was expecting an AbstractClosure");
    	}
        AbstractClosure proc = (AbstractClosure)closure;
        Map bindings = new HashMap(proc.getNumberOfRequiredArguments());
        if (arguments.length < proc.getNumberOfRequiredArguments()) {
            throw new GenyrisException("Too few arguments supplied to proc: " + proc.getName());
        }
        int i = 0;
        Exp formals = proc._lambdaExpression.cdr().car();
        while(formals != NIL) {
            if(!(formals instanceof Pair)) {
                break; // skip the return class
            }
            Exp formal = formals.car();
            if (formal == REST) {
                Exp actuals = assembleListFromRemainingArgs(arguments, i);
                formal = formals.cdr().car();
                if (formal != NIL) {
                    bindings.put(formal, actuals);
                }
                i++;
                break;
            } else if (formal != NIL) {
                if(formal instanceof Pair) {
                    Exp left = formal.car();
                    Exp right = formal.cdr();
                    if(!(left instanceof Symbol) ) {
                        throw new GenyrisException("function argument not a symbol: "
                                + left.toString());
                    }
                    if(!(right instanceof Symbol) ) {
                        throw new GenyrisException("function argument class spec not a symbol: "
                                + right.toString());
                    }
                    Exp klass = proc.getEnv().lookupVariableValue((Symbol)right); 
                    try {
                        TagFunction.validateObjectInClass(proc.getEnv(), arguments[i], (Dictionary)klass);
                    }
                    catch (GenyrisException e) {
                        throw new GenyrisException("Type mismatch in function call for " + left);
                    }
                    bindings.put((Symbol)left, arguments[i]);
                }
                else if (!(formal instanceof Symbol)) {
                    throw new GenyrisException("function argument not a symbol: "
                            + formal.toString());
                } else {
                    bindings.put((Symbol)formal, arguments[i]);
                }
            }
            formals = formals.cdr();
            i++;
        }

        // Use the procedure's frame to get lexical scope
        // and the dynamic environment for the object stuff.
        Environment newEnv = new DynamicEnvironment(proc.getEnv(), bindings, envForBindOperations);
        Exp result = proc.getBody().evalSequence(newEnv);
        Dictionary returnClass = proc.getReturnClassOrNull();
        if(returnClass != null) {
            try {
                TagFunction.validateObjectInClass(proc.getEnv(), result, returnClass);
            }
            catch(GenyrisTypeMismatchException e) {
                throw new GenyrisTypeMismatchException("return type " + e.getMessage());
            }
        }
        return result;
    }

    private Exp assembleListFromRemainingArgs(Exp[] arguments, int i) throws GenyrisException,
            AccessException {
        if(arguments.length <= i) {
            return NIL;
        }
        Pair actuals = new Pair(arguments[i], NIL);
        Pair tail = actuals;
        for (int j = i + 1; j < arguments.length; j++) {
            Pair newTail = new Pair(arguments[j], NIL);
            tail.setCdr(newTail);
            tail = newTail;
        }
        return actuals;
    }

    public String toString() {
        return "<" + getName() + ">";
    }
}
