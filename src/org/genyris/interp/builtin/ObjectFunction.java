// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.Symbol;
import org.genyris.dl.Triple;
import org.genyris.dl.TripleSet;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;

public class ObjectFunction extends ApplicableFunction {

	public ObjectFunction(Interpreter interp) {
		super(interp, "dict", false);
	}

	public Exp bindAndExecute(Closure ignored, Exp[] arguments, Environment env)
			throws GenyrisException {
		Lobject dict = new Lobject(env);
		for (int i = 0; i < arguments.length; i++) {
			if (!arguments[i].listp())
				throw new GenyrisException("argument to dict not a list");
			dict.defineVariable(arguments[i].car(), Evaluator.eval(env,
					arguments[i].cdr()));
		}
		return dict;
	}

	public static abstract class AbstractDictionaryMethod extends AbstractMethod {

		public AbstractDictionaryMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected Lobject getSelfAsDictionary(Environment env) throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof Lobject)) {
				throw new GenyrisException(
						"Non-Dictionary passed to a Dictionary method.");
			} else {
				return (Lobject) _self;
			}
		}
	}

	public static class AsTriplesMethod extends AbstractDictionaryMethod {

		public AsTriplesMethod(Interpreter interp) {
			super(interp, "asTriples");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Lobject self = getSelfAsDictionary(env);
			checkArguments(arguments, 0);
			Exp alist = self.asAlist().cdr();
			Exp results = NIL;
			while(alist != NIL) {
				results = new Lcons(new Triple(self, (Symbol)(alist.car().car()), alist.car().cdr()), results);
				alist = alist.cdr();
			}
			return results;
		}
	}

	public static class AsTripleSetMethod extends AbstractDictionaryMethod {

		public AsTripleSetMethod(Interpreter interp) {
			super(interp, "asTripleSet");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Lobject self = getSelfAsDictionary(env);
			checkArguments(arguments, 0);
			Exp alist = self.asAlist().cdr();
			TripleSet results = new TripleSet();
			while(alist != NIL) {
				results.add(new Triple(self, (Symbol)(alist.car().car()), alist.car().cdr()));
				alist = alist.cdr();
			}
			return results;
		}
	}
}
