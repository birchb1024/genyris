// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.dl.Triple;
import org.genyris.dl.TripleSet;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class ObjectFunction extends ApplicableFunction {

	public ObjectFunction(Interpreter interp) {
		super(interp, "dict", false);
	}

	public Exp bindAndExecute(Closure ignored, Exp[] arguments, Environment env)
			throws GenyrisException {
		Dictionary dict = new Dictionary(env);
		for (int i = 0; i < arguments.length; i++) {
			if (!arguments[i].isPair())
				throw new GenyrisException("argument to dict not a list");
			if (!(arguments[i].car() instanceof DynamicSymbol))
				throw new GenyrisException("argument to dict not a dynamic symbol");
			dict.defineVariable((Symbol)arguments[i].car(), 
					arguments[i].cdr().eval(env));
		}
		return dict;
	}

	public static abstract class AbstractDictionaryMethod extends
			AbstractMethod {

		public AbstractDictionaryMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected Dictionary getSelfAsDictionary(Environment env)
				throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof Dictionary)) {
				throw new GenyrisException(
						"Non-Dictionary passed to a Dictionary method.");
			} else {
				return (Dictionary) _self;
			}
		}
	}

	public static class AsTriplesMethod extends AbstractDictionaryMethod {

		public AsTriplesMethod(Interpreter interp) {
			super(interp, "asTriples");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Dictionary self = getSelfAsDictionary(env);
			checkArguments(arguments, 0);
			Exp alist = self.asAlist().cdr();
			// TODO move this code int Dictionary as a method
			// TODO also collect ExpWithEmbeddedClasses classes triples.

			Exp results = NIL;
			while (alist != NIL) {
				results = new Pair(new Triple(self,
						(SimpleSymbol) (alist.car().car()), alist.car().cdr()),
						results);
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
			Dictionary self = getSelfAsDictionary(env);
			checkArguments(arguments, 0);
			// TODO move this code int Dictionary
			// TODO also collect ExpWithEmbeddedClasses classes triples.
			Exp alist = self.asAlist().cdr();
			TripleSet results = new TripleSet();
			while (alist != NIL) {
				results.add(new Triple(self, (SimpleSymbol) (alist.car().car()),
						alist.car().cdr()));
				alist = alist.cdr();
			}
			return results;
		}
	}

	public static void bindFunctionsAndMethods(Interpreter interpreter)
			throws UnboundException, GenyrisException {
		interpreter.bindMethodInstance(Constants.DICTIONARY,
				new ObjectFunction.AsTriplesMethod(interpreter));
		interpreter.bindMethodInstance(Constants.DICTIONARY,
				new ObjectFunction.AsTripleSetMethod(interpreter));
	}
}
