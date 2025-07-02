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
		AbstractGraph ts = new GraphHashSimple();
		for (int i = 0; i < arguments.length; i++) {
			addTripleFromList(ts, arguments[i]);
		}
		return ts;
	}

	private void addTripleFromList(AbstractGraph ts, Exp exp)
			throws GenyrisException {
		Symbol subject = toSymbol(exp.car());
        Symbol predicate = toSymbol(exp.cdr().car());
        Exp object = exp.cdr().cdr().car();

		ts.add(new Triple(subject, predicate, object));
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
		interpreter.bindMethodInstance(Constants.GRAPH,
				new SubjectsMethod(interpreter));
        interpreter.bindMethodInstance(Constants.GRAPH,
                new UnionMethod(interpreter));
	}

	public abstract static class AbstractGraphMethod extends
			AbstractMethod {

		public AbstractGraphMethod(Interpreter interp, String name) {
			super(interp, name);
		}

		protected AbstractGraph getSelfGraph(Environment env)
				throws GenyrisException {
			getSelf(env);
			if (!(_self instanceof AbstractGraph)) {
				throw new GenyrisException(
						"Non-Graph passed to a Graph method.");
			} else {
				return (AbstractGraph) _self;
			}
		}
	}

	public static class AddMethod extends AbstractGraphMethod {

		public AddMethod(Interpreter interp) {
			super(interp, "add");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
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

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
			checkArguments(arguments, 2);
			Class[] types = { Exp.class, Exp.class };
			checkArgumentTypes(types, arguments);
			return self.get(toSymbol(arguments[0]), toSymbol(arguments[1]));
		}
	}

	public static class GetListMethod extends AbstractGraphMethod {

		public GetListMethod(Interpreter interp) {
            super(interp, "get-list");
        }

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
			checkArguments(arguments, 2);
			Class[] types = { Exp.class, Exp.class };
			checkArgumentTypes(types, arguments);
			return self.getList(toSymbol(arguments[0]), toSymbol(arguments[1]), NIL);
		}
	}

	public static class PutMethod extends AbstractGraphMethod {

		public PutMethod(Interpreter interp) {
			super(interp, "put");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
			checkArguments(arguments, 3);
			Class[] types = { Exp.class, Exp.class, Exp.class };
			checkArgumentTypes(types, arguments);
			self.put(toSymbol(arguments[0]), toSymbol(arguments[1]), arguments[2]);
			return _self;
		}
	}

    public static class SubjectsMethod extends AbstractGraphMethod {

        public SubjectsMethod(Interpreter interp) {
            super(interp, "subjects");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            AbstractGraph self = getSelfGraph(env);
            return self.subjects(NIL);
        }
    }

	public static class UnionMethod extends AbstractGraphMethod {

		public UnionMethod(Interpreter interp) {
			super(interp, "union");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
            checkArguments(arguments, 1);
            Class[] types = { AbstractGraph.class };
            checkArgumentTypes(types, arguments);
			AbstractGraph self = getSelfGraph(env);
			AbstractGraph other = (AbstractGraph)arguments[0]; 
			return self.union(other);
		}
	}

	public static class LengthMethod extends AbstractGraphMethod {

		public LengthMethod(Interpreter interp) {
			super(interp, "length");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
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
			AbstractGraph self = getSelfGraph(env);
			checkMinArguments(arguments, 3);
			Class[] types = { Exp.class, Exp.class, Exp.class };
			checkArgumentTypes(types, arguments);
			Symbol subject = toSymbol(arguments[0]);
			Symbol predicate = toSymbol(arguments[1]);
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
/* #TODO			if (arguments.length == 4 && arguments[3] != NIL) {
				closure = (Closure) arguments[3];
			}
*/
			return self.select(subject, predicate, object, closure, env);
		}
	}

	public static class AsTriplesMethod extends AbstractGraphMethod {

		public AsTriplesMethod(Interpreter interp) {
			super(interp, "asTriples");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
			return self.asTripleList(NIL);
		}
	}

	public static class RemoveMethod extends AbstractGraphMethod {

		public RemoveMethod(Interpreter interp) {
			super(interp, "remove");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			AbstractGraph self = getSelfGraph(env);
			checkArguments(arguments, 1);
			Class[] types = { Triple.class };
			checkArgumentTypes(types, arguments);
			self.remove((Triple) arguments[0]);
			return self;
		}
	}

}
