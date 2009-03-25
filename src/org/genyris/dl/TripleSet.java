package org.genyris.dl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

public class TripleSet extends Atom {

	private Set triples;

	public TripleSet() {
		triples = new HashSet();
	}

	public TripleSet(Set triples2) {
		triples = new HashSet(triples2);
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitTripleSet(this);
	}

	public String toString() {
		return "(tripleset" + ")";
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.TRIPLESET();
	}

	public int hashCode() {
		return triples.hashCode();
	}

	public boolean equals(Object compare) {
		if (compare instanceof TripleSet) {
			TripleSet t = (TripleSet) compare;
			return triples.equals(t.triples);
		} else {
			return false;
		}
	}

	public void add(Triple t) {
		triples.add(t);
	}

	public TripleSet select(Exp subject, Exp predicate, Exp object,
			Closure condition, Environment env) throws GenyrisException {
		TripleSet results = new TripleSet();
		Iterator iter = triples.iterator();
		while (iter.hasNext()) {
			Triple t = (Triple) iter.next();
			if (   (subject   != null && t.subject   != subject) 
				|| (predicate != null && t.predicate != predicate)
				|| (object    != null && t.object    != object)) {
					continue; // no match so try next triple				
			}
			if (condition != null) {
				Exp[] arguments = new Exp[3];
				arguments[0] = t.subject;
				arguments[1] = t.predicate;
				arguments[2] = t.object;
				Exp testResult = condition.applyFunction(env, arguments);
				if (testResult == env.getNil()) {
					continue;
				}
			} 
			results.add(t);
		}
		return results;
	}

	public boolean empty() {
		return triples.isEmpty();
	}

	public void remove(Triple triple) {
		triples.remove(triple);
	}

	public Exp asTripleList(Exp NIL) {
		Exp result = NIL;
		Iterator iter = triples.iterator();
		while(iter.hasNext()) {
			Triple t = (Triple) iter.next();
			result = new Pair(t, result);
		}
		return result;
	}

	public TripleSet difference(TripleSet toRemove) throws GenyrisException {
		TripleSet result =  new TripleSet(triples);
		result.triples.removeAll(toRemove.triples);
		return result;
	}
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}


}
