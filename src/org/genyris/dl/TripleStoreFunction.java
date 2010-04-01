//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

import java.lang.reflect.Method;

import org.genyris.core.Bignum;
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

public class TripleStoreFunction extends ApplicableFunction {

	public TripleStoreFunction(Interpreter interp) {
		super(interp, "triplestore", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		TripleStore ts = new TripleStore();
		for (int i = 0; i < arguments.length; i++) {
			addTripleFromList(ts, arguments[i]);
		}
		return ts;
	}

	private void addTripleFromList(TripleStore ts, Exp exp)
			throws GenyrisException {
		ts.add(Triple.mkTripleFromList(exp));
	}

	public static void bindFunctionsAndMethods(Interpreter interpreter)
			throws UnboundException, GenyrisException {
		interpreter.bindGlobalProcedureInstance(new TripleStoreFunction(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE, new AddMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE, new SelectMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE,
				new AsTriplesMethod(interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE, new RemoveMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE, new LengthMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE, new GetMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE, new PutMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.TRIPLESTORE,
				new GetListMethod(interpreter));
	}

	public static abstract class AbstractTripleStoreMethod extends
			AbstractMethod {

		public AbstractTripleStoreMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected TripleStore getSelfTS(Environment env)
				throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof TripleStore)) {
				throw new GenyrisException(
						"Non-TripleStore passed to a TripleStore method.");
			} else {
				return (TripleStore) _self;
			}
		}
	}

	public static class AddMethod extends AbstractTripleStoreMethod {

		public AddMethod(Interpreter interp) {
			super(interp, "add");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.add((Triple) arguments[0]);
			return _self;
		}
	}

	public static class GetMethod extends AbstractTripleStoreMethod {

		public GetMethod(Interpreter interp) {
			super(interp, "get");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			checkArguments(arguments, 2);
			Class[] types = { Exp.class, Symbol.class };
			checkArgumentTypes(types, arguments);
			return self.get(arguments[0], (Symbol) arguments[1]);
		}
	}

	public static class GetListMethod extends AbstractTripleStoreMethod {

		public GetListMethod(Interpreter interp) {
            super(interp, "get-list");
        }

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			checkArguments(arguments, 2);
			Class[] types = { Exp.class, Symbol.class };
			checkArgumentTypes(types, arguments);
			return self.getList(arguments[0], (Symbol) arguments[1], NIL);
		}
	}

	public static class PutMethod extends AbstractTripleStoreMethod {

		public PutMethod(Interpreter interp) {
			super(interp, "put");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			checkArguments(arguments, 3);
			Class[] types = { Exp.class, Symbol.class, Exp.class };
			checkArgumentTypes(types, arguments);
			self.put(arguments[0], (Symbol) arguments[1], arguments[2]);
			return _self;
		}
	}

	public static class LengthMethod extends AbstractTripleStoreMethod {

		public LengthMethod(Interpreter interp) {
			super(interp, "length");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			return new Bignum(self.length());
		}
	}

	public static class SelectMethod extends AbstractTripleStoreMethod {

		public SelectMethod(Interpreter interp) {
			super(interp, "select");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Closure closure = null;
			TripleStore self = getSelfTS(env);
			checkMinArguments(arguments, 3);
			Class[] types = { Exp.class, Symbol.class, Exp.class };
			checkArgumentTypes(types, arguments);
			Exp subject = arguments[0];
			Symbol predicate = (Symbol) arguments[1];
			Exp object = arguments[2];
			if (arguments[0] == NIL) {
				subject = null;
			}
			if (arguments[1] == NIL) {
				predicate = null;
			}
			if (arguments[2] == NIL) {
				object = null;
			}
			if (arguments.length == 4 && arguments[3] != NIL) {
				if (arguments[3] instanceof Closure) {
					closure = (Closure) arguments[3];
				}
			}
			return self.select(subject, predicate, object, closure, env);
		}
	}

	public static class AsTriplesMethod extends AbstractTripleStoreMethod {

		public AsTriplesMethod(Interpreter interp) {
			super(interp, "asTriples");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			return self.asTripleList(NIL);
		}
	}

	public static class RemoveMethod extends AbstractTripleStoreMethod {

		public RemoveMethod(Interpreter interp) {
			super(interp, "remove");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			TripleStore self = getSelfTS(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.remove((Triple) arguments[0]);
			return self;
		}
	}

}
