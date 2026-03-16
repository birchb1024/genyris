// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.genyris.core.*;
import org.genyris.dl.AbstractGraph;
import org.genyris.dl.GraphHashSimple;
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
        if (lhs instanceof Pair) {
            if (rhs instanceof Symbol) {
                if ( rhs == _interp.getSymbolTable().LEFT()) { // ^(1 2)!left
                    return ((Pair) lhs).car();
                }
                if ( rhs == _interp.getSymbolTable().RIGHT()) { // ^(1 2)!right
                    return ((Pair) lhs).cdr();
                }
            } else if (rhs instanceof DynamicSymbol) {
                DynamicSymbol drhs = (DynamicSymbol) rhs;
                Symbol slhs = drhs.getRealSymbol();
                if ( slhs == _interp.getSymbolTable().LEFT()) { // ^(1 2)!.left
                    return ((Pair) lhs).car();
                }
                if ( slhs == _interp.getSymbolTable().RIGHT()) { // ^(1 2)!.right
                    return ((Pair) lhs).cdr();
                }
            } else if (rhs instanceof Bignum) {
                Pair p = (Pair)lhs;
                return p.nth(((Bignum)rhs).bigDecimalValue().intValue(), NIL);
            }
        }
        if (lhs instanceof AbstractGraph && rhs instanceof Symbol) {
            return ((AbstractGraph)lhs).shift((Symbol)rhs, env);
        }
        Exp erhs = rhs.eval(E);
        if ((lhs instanceof Pair && erhs instanceof Bignum)
                || ( lhs instanceof AbstractGraph && erhs instanceof Symbol) ) {
            Exp[] next = new  Exp[2];
            next[0] = arguments[0]  ;
            next[1] = erhs;
            return bindAndExecute(proc, next, env);
        }
        return erhs;
    }
}
