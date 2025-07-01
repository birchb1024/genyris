package org.genyris.dl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

// TODO actually make this class use Maps
public class GraphHashSimple extends AbstractGraph {

	List triples;

	public GraphHashSimple() {
		triples = new CopyOnWriteArrayList();
	}

	public GraphHashSimple(Set triples2) {
		Iterator iter = triples2.iterator();
		while (iter.hasNext()) {
			add((Triple)iter.next());
		}
	}

	@Override
    public Iterator iterator() {
	    //
	    // Iterate over the triples in the Graph.
	    //
	    return triples.iterator();
	}
	@Override
    public String toString() {
		return "(graph" + ")";
	}

	@Override
    public Symbol getBuiltinClassSymbol(Internable table) {
		return table.GRAPH();
	}

	@Override
    public int hashCode() {
		return triples.hashCode();
	}

	@Override
    public int length() {
		return triples.size();
	}

	@Override
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
	
	@Override
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

	@Override
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

	@Override
    public AbstractGraph select(Exp subject, Exp predicate, Exp object,
			Closure condition, Environment env) throws GenyrisException {
		GraphHashSimple results = new GraphHashSimple();
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

	@Override
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

	@Override
    public Exp getList(Exp subject, Symbol predicate, Exp NIL) {
		Exp result = NIL;
		List sorted = new ArrayList(triples);
		Collections.sort(sorted);
		Collections.reverse(sorted);
		Iterator iter = sorted.iterator();
		while (iter.hasNext()) {
			Triple item = (Triple)iter.next();
 			if (   (item.subject   == subject) 
				&& (item.predicate == predicate)) {
 					result = new Pair(item.object, result);	
			}
		}
		return result;
	}
	
	@Override
    public boolean empty() {
		return triples.isEmpty();
	}

	@Override
    public void remove(Triple triple) {
		triples.remove(triple);
	}

	@Override
    public Exp asTripleList(Exp NIL) {
		Exp result = NIL;
		List sorted = new ArrayList(triples);
		Collections.sort(sorted);
		Collections.reverse(sorted);
		Iterator iter = sorted.iterator();
		while(iter.hasNext()) {
			Triple item = (Triple)iter.next();
			result = new Pair(new Triple(item.subject, (SimpleSymbol) item.predicate, item.object ), result);
		}
		return result;
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

	@Override
    public Exp subjects(Exp NIL) {
		Exp result = NIL;
		List sorted = new ArrayList(triples);
		Collections.sort(sorted);
		Collections.reverse(sorted);
		Iterator iter = sorted.iterator();
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
