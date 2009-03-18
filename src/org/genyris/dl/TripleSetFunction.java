//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class TripleSetFunction extends ApplicableFunction {
	// TODO move most of this logic into the TripleSet class

	public TripleSetFunction(Interpreter interp) {
		super(interp, "tripleset", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		TripleSet ts = new TripleSet();
		for (int i = 0; i < arguments.length; i++) {
			addTripleFromList(ts, arguments[i]);
		}
		return ts;
	}

	private void addTripleFromList(TripleSet ts, Exp exp) throws GenyrisException {
		Exp subject = exp.car();
		Exp predicate = exp.cdr().car();
		Exp object = exp.cdr().cdr().car();
		if (!(predicate instanceof Symbol)) {
			throw new GenyrisException(getName()
					+ " was expecting a Symbol predicate, got: " + predicate);
		}
		ts.add(new Triple(subject, (Symbol) predicate, object));
	}

	public static void bindFunctionsAndMethods(Interpreter interp) throws UnboundException, GenyrisException {
		interp.bindGlobalProcedure(TripleSetFunction.class);
		interp.bindMethod(Constants.TRIPLESET, AddMethod.class);
		interp.bindMethod(Constants.TRIPLESET, SelectMethod.class);
		interp.bindMethod(Constants.TRIPLESET, AsTriplesMethod.class);
		interp.bindMethod(Constants.TRIPLESET, RemoveMethod.class);
	}
	public static abstract class AbstractTripleSetMethod extends AbstractMethod {

		public AbstractTripleSetMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected TripleSet getSelfTS(Environment env) throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof TripleSet)) {
				throw new GenyrisException(
						"Non-TripleSet passed to a TripleSet method.");
			} else {
				return (TripleSet) _self;
			}
		}
	}

	public static class AddMethod extends AbstractTripleSetMethod {

		public AddMethod(Interpreter interp) {
			super(interp, "add");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleSet self = getSelfTS(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.add((Triple) arguments[0]);
			return _self;
		}
	}

	public static class SelectMethod extends AbstractTripleSetMethod {

		public SelectMethod(Interpreter interp) {
			super(interp, "select");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Closure closure = null;
			TripleSet self = getSelfTS(env);
			checkMinArguments(arguments, 3);
			Class[] types = { Exp.class, Symbol.class, Exp.class};
			checkArgumentTypes(types, arguments);
			Exp subject = arguments[0];
			Symbol predicate = (Symbol)arguments[1];
			Exp object = arguments[2];
			if(arguments[0] == NIL) {
				subject = null;
			}
			if(arguments[1] == NIL) {
				predicate = null;
			}
			if(arguments[2] == NIL) {
				object = null;
			}
			if(arguments.length == 4) {
				if(arguments[3] instanceof Closure) {
					closure = (Closure) arguments[3];
				}
			}
			return self.select(subject, predicate, object, closure, env);
		}
	}

	public static class AsTriplesMethod extends AbstractTripleSetMethod {

		public AsTriplesMethod(Interpreter interp) {
			super(interp, "asTriples");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleSet self = getSelfTS(env);
			return self.asTripleList(NIL);
		}
	}
	public static class RemoveMethod extends AbstractTripleSetMethod {

		public RemoveMethod(Interpreter interp) {
			super(interp, "remove");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleSet self = getSelfTS(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.remove((Triple) arguments[0]);
			return self;
		}
	}

}
