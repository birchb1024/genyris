// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.builtin.TagFunction;

public class ClassicFunction extends ApplicableFunction {
    private Exp REST;

    public ClassicFunction(Interpreter interp) {
        super(interp);
        REST = interp.getSymbolTable().internString(Constants.REST);
    }

    public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException {
        AbstractClosure proc = (AbstractClosure)closure; // TODO run time validation
        Map bindings = new HashMap();
        if (arguments.length < proc.getNumberOfRequiredArguments()) {
            throw new GenyrisException("Too few arguments supplied to proc: " + proc.getName());
        }
        for (int i = 0; i < arguments.length; i++) {
            Exp formal = proc.getArgumentOrNIL(i);
            if (formal == REST) {
                Lcons actuals = assembleListFromRemainingArgs(arguments, i);
                formal = proc.getLastArgumentOrNIL();
                if (formal != NIL) {
                    bindings.put(formal, actuals);
                }
                break;
            } else if (formal != NIL) {
                if(formal instanceof Lcons) {
                    Exp left = formal.car();
                    Exp right = formal.cdr();
                    if(!(left instanceof Lsymbol) ) {
                        throw new GenyrisException("function argument not a symbol: "
                                + left.toString());
                    }
                    if(!(right instanceof Lsymbol) ) {
                        throw new GenyrisException("function argument class spec not a symbol: "
                                + right.toString());
                    }
                    Exp klass = proc.getEnv().lookupVariableValue(right); // TODO - move to def for speedup.
                    try {
                        TagFunction.validateObjectInClass(proc.getEnv(), arguments[i], (Lobject)klass);
                    }
                    catch (GenyrisException e) {
                        throw new GenyrisException("Type mismatch in function call for " + left);
                    }
                    bindings.put(left, arguments[i]);
                }
                else if (!(formal instanceof Lsymbol)) {
                    throw new GenyrisException("function argument not a symbol: "
                            + formal.toString());
                } else {
                    Lsymbol sym = (Lsymbol)formal;
                    if (sym.isMember()) {
                        throw new GenyrisException("cannot define member in function arguments: "
                                + sym.toString());
                    }
                    bindings.put(formal, arguments[i]);
                }
            }
        }
        // Use the procedure's frame to get lexical scope
        // and the dynamic environment for the object stuff.
        Environment newEnv = new SpecialEnvironment(proc.getEnv(), bindings, envForBindOperations);
        Exp result = Evaluator.evalSequence(newEnv, proc.getBody());
        Exp returnClass = proc.getReturnClassOrNIL();
        if(returnClass != NIL) {
            try {
                TagFunction.validateObjectInClass(proc.getEnv(), result, (Lobject)returnClass); // TODO unsafe downcast
            }
            catch(GenyrisTypeMismatchException e) {
                throw new GenyrisTypeMismatchException("return type " + e.getMessage());
            }
        }
        return result;
    }

    private Lcons assembleListFromRemainingArgs(Exp[] arguments, int i) throws GenyrisException,
            AccessException {
        Lcons actuals = new Lcons(arguments[i], NIL);
        Lcons tail = actuals;
        for (int j = i + 1; j < arguments.length; j++) {
            Lcons newTail = new Lcons(arguments[j], NIL);
            tail.setCdr(newTail);
            tail = newTail;
        }
        return actuals;
    }

    public Object getJavaValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
