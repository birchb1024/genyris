// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.math;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class AbstractMathFunction extends ApplicableFunction {

    private int _minExpectedNumberOfArguments;

    public AbstractMathFunction(Interpreter interp,
            String name, int minExpectedNumberOfArguments) {
        super(interp, name, true);
        _minExpectedNumberOfArguments = minExpectedNumberOfArguments;
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
        if (arguments.length < _minExpectedNumberOfArguments)
            throw new GenyrisException("Too few arguments to " + getName());
        try {
            if(arguments.length == 1) {
                return mathOperation(arguments[0]);
            }
            Exp result = arguments[0];
            for (int i = 1; i < arguments.length; i++) {
                result = mathOperation(result, arguments[i]);
            }
            return result;
        } catch (RuntimeException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    protected Exp mathOperation(Exp unary) throws GenyrisException {
        throw new GenyrisException("Bad call to mathOperation(Exp unary)");
    }

    protected Exp mathOperation(Exp a, Exp b) throws GenyrisException{
        throw new GenyrisException("Bad call to mathOperation(Exp a, Exp b)");
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new PlusFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new MinusFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new MultiplyFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new DivideFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new RemainderFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new GreaterThanFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new LessThanFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new PowerFunction(interpreter));

    }

}