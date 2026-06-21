// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.*;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class BackquoteFunction extends ApplicableFunction {

	private Exp DOLLAR, AT;

	public BackquoteFunction(Interpreter interp) {
		super(interp, Constants.TEMPLATE, false);
		DOLLAR = interp.getSymbolTable().DOLLAR();
		AT = interp.getSymbolTable().DOLLAR_AT();
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 1);
		return backQuoteAux(envForBindOperations, arguments[0]);
	}

	private Exp backQuoteAux(Environment env, Exp sexp) throws GenyrisException {
		if (sexp == NIL || (!sexp.isPair())) {
			return sexp;
		} else {
			Pair list = (Pair) sexp;
			if (list.car() == DOLLAR) {
				return list.cdr().car().eval(env);
			}
			if (list.car().isPair() && list.car().car() == AT) {
				Exp res = list.car().cdr().car().eval(env);
				Exp rest = backQuoteAux(env, list.cdr());
				return append(res, rest);
			}
			if (list instanceof PairSource) {
				return new PairSource(backQuoteAux(env, list.car()),
						backQuoteAux(env, list.cdr()),
						((PairSource)list).filename, ((PairSource)list).lineNumber);
			}
			return new Pair(backQuoteAux(env, list.car()), backQuoteAux( env, list.cdr()));
		}
	}

	private Exp append(Exp l1, Exp l2) throws AccessException {
		if (l1 == NIL) {
			return l2;
		}
		if (l1 instanceof PairEquals) {
			return new PairEquals(l1.car(), append(l1.cdr(), l2));
		}
		if (l1 instanceof PairSource) {
			PairSource tmp = new PairSource(l1.car(), append(l1.cdr(), l2), ((PairSource)l1).filename, ((PairSource)l1).lineNumber);
			return tmp;
		}
		return new Pair(l1.car(), append(l1.cdr(), l2));
	}
}
