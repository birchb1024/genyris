//Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl;

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

public class GraphFunction extends ApplicableFunction {

	public GraphFunction(Interpreter interp) {
		super(interp, "graph", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		Graph ts = new Graph();
		for (int i = 0; i < arguments.length; i++) {
			addTripleFromList(ts, arguments[i]);
		}
		return ts;
	}

	private void addTripleFromList(Graph ts, Exp exp)
			throws GenyrisException {
		ts.add(Triple.mkTripleFromList(exp));
	}

	public static void bindFunctionsAndMethods(Interpreter interpreter)
			throws UnboundException, GenyrisException {
		interpreter.bindGlobalProcedureInstance(new GraphFunction(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH, new AddMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH, new SelectMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH,
				new AsTriplesMethod(interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH, new RemoveMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH, new LengthMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH, new GetMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH, new PutMethod(
				interpreter));
		interpreter.bindMethodInstance(Constants.GRAPH,
				new GetListMethod(interpreter));
	}

	public static abstract class AbstractGraphMethod extends
			AbstractMethod {

		public AbstractGraphMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected Graph getSelfTS(Environment env)
				throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof Graph)) {
				throw new GenyrisException(
						"Non-Graph passed to a Graph method.");
			} else {
				return (Graph) _self;
			}
		}
	}

	public static class AddMethod extends AbstractGraphMethod {

		public AddMethod(Interpreter interp) {
			super(interp, "add");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.add((Triple) arguments[0]);
			return _self;
		}
	}

	public static class GetMethod extends AbstractGraphMethod {

		public GetMethod(Interpreter interp) {
			super(interp, "get");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			checkArguments(arguments, 2);
			Class[] types = { Exp.class, Symbol.class };
			checkArgumentTypes(types, arguments);
			return self.get(arguments[0], (Symbol) arguments[1]);
		}
	}

	public static class GetListMethod extends AbstractGraphMethod {

		public GetListMethod(Interpreter interp) {
            super(interp, "get-list");
        }

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			checkArguments(arguments, 2);
			Class[] types = { Exp.class, Symbol.class };
			checkArgumentTypes(types, arguments);
			return self.getList(arguments[0], (Symbol) arguments[1], NIL);
		}
	}

	public static class PutMethod extends AbstractGraphMethod {

		public PutMethod(Interpreter interp) {
			super(interp, "put");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			checkArguments(arguments, 3);
			Class[] types = { Exp.class, Symbol.class, Exp.class };
			checkArgumentTypes(types, arguments);
			self.put(arguments[0], (Symbol) arguments[1], arguments[2]);
			return _self;
		}
	}

	public static class LengthMethod extends AbstractGraphMethod {

		public LengthMethod(Interpreter interp) {
			super(interp, "length");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			return new Bignum(self.length());
		}
	}

	public static class SelectMethod extends AbstractGraphMethod {

		public SelectMethod(Interpreter interp) {
			super(interp, "select");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Closure closure = null;
			Graph self = getSelfTS(env);
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

	public static class AsTriplesMethod extends AbstractGraphMethod {

		public AsTriplesMethod(Interpreter interp) {
			super(interp, "asTriples");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			return self.asTripleList(NIL);
		}
	}

	public static class RemoveMethod extends AbstractGraphMethod {

		public RemoveMethod(Interpreter interp) {
			super(interp, "remove");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			Graph self = getSelfTS(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.remove((Triple) arguments[0]);
			return self;
		}
	}

}
