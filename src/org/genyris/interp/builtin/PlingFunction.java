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
        if (lhs instanceof Dictionary) {
            if (rhs instanceof DynamicSymbol) { // d!.w
                Dictionary dict = (Dictionary) lhs;
                return ((DynamicSymbol)rhs).eval(dict);
            }
            if (rhs instanceof SimpleSymbol) { // d!w
                DynamicSymbol var = new DynamicSymbol((SimpleSymbol) rhs);
                Dictionary dict = (Dictionary) lhs;
                return var.eval(dict);
            }
            else {
                    throw new GenyrisException("pling Dictionary unimplemented: " +  _interp.arrayToList(arguments));
            }
        } else if (lhs instanceof Pair) {
            Pair p = (Pair)lhs;
            if (rhs instanceof Bignum) {  // ^(q w e)!2
                return p.nth(((Bignum)rhs).bigDecimalValue().intValue(), NIL);
            }
            PairEnvironment pe = new PairEnvironment(env, p);
            if (rhs instanceof DynamicSymbol) {
                return ((DynamicSymbol)rhs).eval(pe);
            }
            if (rhs instanceof SimpleSymbol) {
                return new DynamicSymbol((SimpleSymbol)rhs).eval(pe);
            }
            throw new GenyrisException("pling on type Pair Unimplemented: " + _interp.arrayToList(arguments));
        }
        else {
            Environment e = lhs.makeEnvironment(env);
            if (rhs instanceof DynamicSymbol) {
                return ((DynamicSymbol)rhs).eval(e);
            }
            if (rhs instanceof SimpleSymbol) {
                return new DynamicSymbol((SimpleSymbol)rhs).eval(e);
            }
            return rhs.eval(e);
//		    throw new GenyrisException("pling Unimplemented " + _interp.arrayToList(arguments));
	    }
    }
}
