//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class TripleFunction extends ApplicableFunction {

	public TripleFunction(Interpreter interp) {
		super(interp, "triple", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 3);
		Class[] types = { Exp.class, Symbol.class, Exp.class };
		checkArgumentTypes(types, arguments);
		return new Triple(arguments[0], (Symbol) arguments[1], arguments[2]);
	}

	public static abstract class AbstractTripleMethod extends AbstractMethod {

		public AbstractTripleMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected Triple getSelfAsTriple(Environment env) throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof Triple)) {
				throw new GenyrisException(
						"Non-Triple passed to a Triple method.");
			} else {
				return (Triple) _self;
			}
		}
	}

	public static class SubjectMethod extends AbstractTripleMethod {

		public SubjectMethod(Interpreter interp) {
			super(interp, "subject");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Triple self = getSelfAsTriple(env);
			checkArguments(arguments, 0);
			return self.subject;
		}
	}

	public static class PredicateMethod extends AbstractTripleMethod {

		public PredicateMethod(Interpreter interp) {
			super(interp, "predicate");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Triple self = getSelfAsTriple(env);
			checkArguments(arguments, 0);
			return self.predicate;
		}
	}

	public static class ObjectMethod extends AbstractTripleMethod {

		public ObjectMethod(Interpreter interp) {
			super(interp, "object");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Triple self = getSelfAsTriple(env);
			checkArguments(arguments, 0);
			return self.object;
		}
	}

}
