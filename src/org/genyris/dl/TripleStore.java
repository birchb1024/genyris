package org.genyris.dl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

public class TripleStore extends Atom {

	private List triples;

	public TripleStore() {
		triples = new CopyOnWriteArrayList();
	}

	public TripleStore(Set triples2) {
		Iterator iter = triples2.iterator();
		while (iter.hasNext()) {
			add((Triple)iter.next());
		}
	}

	public boolean equals(Object compare) {
		if (!(compare instanceof TripleStore)) {
			return false;
		} else {
			int matches = 0;
			TripleStore ts = (TripleStore) compare;
			Iterator iter = triples.iterator();
			while (iter.hasNext()) {
				Triple item = (Triple)iter.next();
				if(ts.contains(item.subject, item.predicate, item.object)) {
					matches++;
				}
			}
			if ( matches == triples.size()) {
				return true;
			}
			return false;
		}
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitTripleStore(this);
	}

	public String toString() {
		return "(triplestore" + ")";
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.TRIPLESTORE();
	}

	public int hashCode() {
		return triples.hashCode();
	}


	public boolean contains(Exp subject, Exp predicate, Exp object) {
		Iterator iter = triples.iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject.equals(subject)
					&& rec.predicate == predicate
					&& rec.object.equals(object)) {
				return true;
			}
		}
		return false;
	}
	
	public void remove(Exp subject, Exp predicate, Exp object) {
		Iterator iter = triples.iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject.equals(subject)
					&& rec.predicate == predicate
					&& rec.object.equals(object)) {
				triples.remove(rec);
			}
		}
	}

	public void add(Triple t) {
		Iterator iter = triples.iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject.equals(t.subject)
					&& rec.predicate == t.predicate
					&& rec.object.equals(t.object)) {
				return;
			}
		}
		triples.add(t);
	}

	public TripleStore select(Exp subject, Exp predicate, Exp object,
			Closure condition, Environment env) throws GenyrisException {
		TripleStore results = new TripleStore();
		Iterator iter = triples.iterator();
		while (iter.hasNext()) {
			Triple item = (Triple)iter.next();
 			if (   (subject   != null && item.subject   != subject) 
				|| (predicate != null && item.predicate != predicate)
				|| (object    != null && item.object    != object)) {
					continue; // no match so try next triple				
			}
			if (condition != null) {
				Exp[] arguments = new Exp[3];
				arguments[0] = item.subject;
				arguments[1] = item.predicate;
				arguments[2] = item.object;
				Exp testResult = condition.applyFunction(env, arguments);
				if (testResult == env.getNil()) {
					continue;
				}
			} 
			results.add(new Triple(item.subject, (SimpleSymbol) item.predicate, item.object ));
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
			Triple item = (Triple)iter.next();
			result = new Pair(new Triple(item.subject, (SimpleSymbol) item.predicate, item.object ), result);
		}
		return result;
	}

	public TripleStore difference(TripleStore toRemove) throws GenyrisException {
		TripleStore result =  new TripleStore();
		Iterator iter = triples.iterator();
		while(iter.hasNext()) {
			Triple item = (Triple)iter.next();
			remove(item.subject, item.predicate, item.object);
		}
		return result;
	}
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}


}
