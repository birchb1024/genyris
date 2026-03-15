// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.*;

public class PlingFunction extends ApplicableFunction {

    private static final Log log = LogFactory.getLog(PlingFunction.class);

    public PlingFunction(Interpreter interp) {
		super(interp, "pling", false);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {
        Exp lhs = arguments[0].eval(env);
        Exp rhs = arguments[1];
        Environment E = lhs.makeEnvironment(env);

        if ((lhs instanceof Dictionary || lhs instanceof StrinG || lhs instanceof Bignum ) && rhs instanceof SimpleSymbol) { // <dict>!w
                rhs = new DynamicSymbol((SimpleSymbol) rhs);
        }
        if (lhs instanceof Pair &&  rhs instanceof Symbol) {
            if ( rhs == _interp.getSymbolTable().LEFT()) { // ^(1 2)!left
                return ((Pair) lhs).car();
            }
            if ( rhs == _interp.getSymbolTable().RIGHT()) { // ^(1 2)!right
                return ((Pair) lhs).cdr();
            }
        }
        if (lhs instanceof Pair && rhs instanceof DynamicSymbol) {
            DynamicSymbol drhs = (DynamicSymbol) rhs;
            Symbol slhs = drhs.getRealSymbol();
            if ( slhs == _interp.getSymbolTable().LEFT()) { // ^(1 2)!.left
                return ((Pair) lhs).car();
            }
            if ( slhs == _interp.getSymbolTable().RIGHT()) { // ^(1 2)!.right
                return ((Pair) lhs).cdr();
            }
        }
        Exp erhs = rhs.eval(E);
        if (lhs instanceof Pair && erhs instanceof Bignum) {
            Pair p = (Pair)lhs;
            return p.nth(((Bignum)erhs).bigDecimalValue().intValue(), NIL);
        }
        return erhs;
    }
}
