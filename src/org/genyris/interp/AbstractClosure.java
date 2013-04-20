// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Atom;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.StandardClass;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public abstract class AbstractClosure extends Atom implements Closure {

    private final Environment _env;
    final Exp _lambdaExpression;
    final ApplicableFunction _functionToApply;
    protected int _numberOfRequiredArguments;
    private StandardClass _returnClass;
    private boolean _restArgs;

    public AbstractClosure(Environment environment, Exp expression,
            ApplicableFunction appl) {
        _env = environment;
        _lambdaExpression = expression;
        _functionToApply = appl;
        _numberOfRequiredArguments = -1;
        _returnClass = null;
        _restArgs = false;
    }

    private Symbol REST() {
        return _env.getSymbolTable().REST();
    }

    private Symbol NIL() {
        return _env.getNil();
    }

    private int countFormalArguments(Exp exp) throws AccessException {
        int count = 0;
        while (exp != NIL()) {
            if (!(exp instanceof Pair)) { // ignore trailing type
                // specification
                break;
            }
            if (((Pair) exp).car() == REST()) {
                _restArgs = true;
                return count;
            }
            count += 1;
            exp = exp.cdr();
        }
        return count;
    }

    public Exp getArgumentOrNIL(int index) throws GenyrisException {
        try {
            return _lambdaExpression.cdr().car().nth(index, NIL());
        } catch (AccessException e) {
            throw new GenyrisException("Additional argument to function "
                    + _lambdaExpression);
        }
    }

    public Exp getBody() throws AccessException {
        return _lambdaExpression.cdr().cdr();
    }

    public Exp getCode() throws AccessException {
        return _lambdaExpression;
    }

    public abstract Exp[] computeArguments(Environment env, Exp exp)
            throws GenyrisException;

    public Exp applyFunction(Environment environment, Exp[] arguments)
            throws GenyrisException {

        return _functionToApply.bindAndExecuteAux(this, arguments, environment); // double
        // dispatch
    }

    public Environment getEnv() {
        return _env;
    }

    public int getNumberOfRequiredArguments() throws AccessException {
        if (_numberOfRequiredArguments < 0) {
            _numberOfRequiredArguments = countFormalArguments(_lambdaExpression
                    .cdr().car());
        }
        return _numberOfRequiredArguments;
    }

    public String getName() {
        return _functionToApply.getName();
    }

    public StandardClass getReturnClassOrNull() throws GenyrisException {
        if (_returnClass != null) {
            return _returnClass;
        }
        Exp args = _lambdaExpression.cdr().car();
        Symbol returnTypeSymbolFound = NIL();
        Exp possibleReturnClass = NIL();
        if (args != NIL()) {
            Exp tmp = args;
            while (tmp.cdr() != NIL()) { // TODO refactor this loop into
                if (tmp.cdr() instanceof Symbol) {
                    returnTypeSymbolFound = (Symbol) tmp.cdr();
                    possibleReturnClass = _env
                            .lookupVariableValue(returnTypeSymbolFound);
                    break;
                }
                tmp = tmp.cdr();
            }
        }
        if (possibleReturnClass == NIL()) {
            return null;
        }
        if (possibleReturnClass instanceof StandardClass) {
            return (_returnClass = (StandardClass) possibleReturnClass);
        }
        throw new GenyrisException(possibleReturnClass
                + " return class not a class.");
    }

    public Environment makeEnvironment(Environment parent)
            throws GenyrisException {
        return new ProcEnvironment(parent, this);
    }

    public Exp dir(Internable table) {
        return Pair.cons2(new DynamicSymbol(table.SOURCE()),
                new DynamicSymbol(table.NAME())
                , Pair.cons3(
                new DynamicSymbol(table.SELF()),
                new DynamicSymbol(table.VARS()), new DynamicSymbol(table
                        .CLASSES()), table.NIL()));
    }

    public void checkTooFewArgumentCount(Exp[] arguments) throws GenyrisException {
        if (arguments.length < getNumberOfRequiredArguments()) {
            throw new GenyrisException("Too few arguments supplied to proc: "
                    + getName() + ". Args were: " + argsToString(arguments));
        }
    }
    public void checkTooManyArgumentCount(Exp[] arguments) throws GenyrisException {
        if (!_restArgs && arguments.length > getNumberOfRequiredArguments()) {
            throw new GenyrisException("Too many arguments supplied to proc: "
                    + getName() + ". Args were: " + argsToString(arguments));
        }
    }
    private StringBuffer argsToString(Exp[] arguments) {
        StringBuffer args = new StringBuffer("(");
        for (int i = 0; i < arguments.length; i++) {
            args.append(arguments[i].toString() + " ");
        }
        args.append(")");
        return args;
    }


}