package org.genyris.dl;

import java.util.ArrayList;
import java.util.Collections;
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
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

public class Graph extends Atom {

	private List triples;

	public Graph() {
		triples = new CopyOnWriteArrayList();
	}

	public Graph(Set triples2) {
		Iterator iter = triples2.iterator();
		while (iter.hasNext()) {
			add((Triple)iter.next());
		}
	}

	public Iterator iterator() {
	    //
	    // Iterate over the triples in the Graph.
	    //
	    return triples.iterator();
	}
	public boolean equals(Object compare) {
		if (!(compare instanceof Graph)) {
			return false;
		} else {
			int matches = 0;
			Graph otherTS = (Graph) compare;
			Iterator iter = iterator();
			while (iter.hasNext()) {
				Triple item = (Triple)iter.next();
				if(otherTS.contains(item.subject, item.predicate, item.object)) {
					matches++;
				}
			}
			if ( matches == triples.size() && matches == otherTS.triples.size()) {
				return true;
			}
			return false;
		}
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitGraph(this);
	}

	public String toString() {
		return "(graph" + ")";
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.GRAPH();
	}

	public int hashCode() {
		return triples.hashCode();
	}

	public int length() {
		return triples.size();
	}

	public boolean contains(Exp subject, Exp predicate, Exp object) {
		Iterator iter = iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject == subject
					&& rec.predicate == predicate
					&& rec.object.equals(object)) {
				return true;
			}
		}
		return false;
	}
	
	public void remove(Exp subject, Exp predicate, Exp object) {
		Iterator iter = iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject == subject
					&& rec.predicate == predicate
					&& rec.object.equals(object)) {
				triples.remove(rec);
			}
		}
	}

	public void add(Triple t) {
		Iterator iter = iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject == t.subject
					&& rec.predicate == t.predicate
					&& rec.object.equals(t.object)) {
				return;
			}
		}
		triples.add(t);
	}

	public Graph select(Exp subject, Exp predicate, Exp object,
			Closure condition, Environment env) throws GenyrisException {
		Graph results = new Graph();
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Triple item = (Triple)iter.next();
 			if (   (subject   != null && item.subject   != subject) 
				|| (predicate != null && item.predicate != predicate)
				|| (object    != null && (!item.object.equals(object)))) {
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

	public Exp get(Exp subject, Exp predicate) throws GenyrisException {
		Exp result = null;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Triple item = (Triple)iter.next();
 			if (   (item.subject   == subject) 
				&& (item.predicate == predicate)) {
 					if(result != null) {
 						throw new GenyrisException("More than one triple in graph matching " + subject.toString() + " " + predicate.toString());
 					} else {
 						result = item.object;	
 					}
			}
		}
		if( result == null ) {
				throw new GenyrisException("No triple in graph matching " + subject.toString() + " " + predicate.toString());
		}
		return result;
	}

	public Exp getList(Exp subject, Symbol predicate, Exp NIL) {
		Exp result = NIL;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Triple item = (Triple)iter.next();
 			if (   (item.subject   == subject) 
				&& (item.predicate == predicate)) {
 					result = new Pair(item.object, result);	
			}
		}
		return result;
	}
	
	public boolean empty() {
		return triples.isEmpty();
	}

	public void remove(Triple triple) {
		triples.remove(triple);
	}

	public Exp asTripleList(Exp NIL) {
		Exp result = NIL;
		List sorted = new ArrayList(triples);
		Collections.sort(sorted);
		Iterator iter = sorted.iterator();
		while(iter.hasNext()) {
			Triple item = (Triple)iter.next();
			result = new Pair(new Triple(item.subject, (SimpleSymbol) item.predicate, item.object ), result);
		}
		return result;
	}

	public Graph difference(Graph toRemove) throws GenyrisException {
		Graph result =  new Graph();
		Iterator iter = iterator();
		while(iter.hasNext()) {
			Triple item = (Triple)iter.next();
			remove(item.subject, item.predicate, item.object);
		}
		return result;
	}

    public Graph union(Graph other) throws GenyrisException {
        Graph result =  new Graph();
        Iterator iter = iterator();
        while(iter.hasNext()) {
            Triple item = (Triple)iter.next();
            result.add(item);
        }
        iter = other.iterator();
        while(iter.hasNext()) {
            Triple item = (Triple)iter.next();
            result.add(item);
        }
        return result;
    }

    public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public void put(Exp subject, Symbol predicate, Exp object) {
		Iterator iter = iterator();
		while(iter.hasNext()) {
			Triple rec = (Triple) iter.next();
			if(rec.subject == subject
					&& rec.predicate == predicate) {
				triples.remove(rec);
			}
		}
		add(new Triple(subject, predicate, object));
	}

	public Exp subjects(Exp NIL) {
		Exp result = NIL;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Triple item = (Triple)iter.next();
			if(!memberp(item.subject, result, NIL)) {
	 			result = new Pair(item.subject, result);					
			}
		}
		return result;
	}

	private static boolean memberp(Exp item, Exp list, Exp NIL) {
		while( list != NIL) {
			try {
				if(item == list.car()) 
				    return true;
				list = list.cdr();
			} catch (AccessException e) {
				return false;
			}		
		}
		return false;
	}


}
