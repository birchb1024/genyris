package org.genyris.dl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.genyris.core.*;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

import java.util.*;

public class GraphHashSimple extends AbstractGraph {

	Multimap<Symbol, Triple> subjects;
	Multimap<Symbol, Triple> predicates;
	Multimap<Exp, Triple> objects;

	public GraphHashSimple() {
		subjects = ArrayListMultimap.create();
		predicates = ArrayListMultimap.create();
		objects = ArrayListMultimap.create();
	}

	public GraphHashSimple(Set triples2) {
		Iterator iter = triples2.iterator();
		while (iter.hasNext()) {
			Triple t = (Triple)iter.next();
			add(t);
			subjects.put((Symbol)t.subject, t);
			predicates.put((Symbol)t.predicate, t);
			objects.put((Symbol)t.object, t);
		}
	}

    @Override
    public Iterator iterator() {
	    //
	    // Iterate over the triples in the Graph.
	    //
	    return subjects.values().iterator();
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
		return subjects.hashCode();
	}

	public int length() {
		return subjects.size();
	}

	public Triple contains(Symbol S, Symbol P, Exp O) {
		Iterator iter = subjects.get(S).iterator();
		while (iter.hasNext()) {
			Triple t = (Triple)iter.next();
			if (t.predicate.equals(P) && t.object.equals(O)) {
				return t;
			}
		}
		return null;
	}

    public void add(Triple t) {
		if ( contains(t.subject, t.predicate, t.object) != null) {
			return;
		}
        subjects.put(t.subject, t);
		predicates.put(t.predicate, t);
		objects.put(t.object, t);
    }

	public AbstractGraph select(Symbol S, Symbol P, Exp O,
            Closure condition, Environment env) throws GenyrisException {
		if (condition != null){
			throw new GenyrisException("Closures not supported in Graph.select, sorry.");
		}
        GraphHashSimple results = new GraphHashSimple();
		// * * *
		if (S== null && P == null && O == null) {
			return this; // #TODO Hmm deep copy?
		}
		// Subject, *, *
		if (O == null && P == null) {
			Iterator Ses = subjects.get(S).iterator();
			while (Ses.hasNext()) {
				Triple t = (Triple) Ses.next();
				results.add(t);
			}
			return results;
		}
		// *, Predicate, *
		if (S == null && O == null) {
			Iterator Ps = predicates.get(P).iterator();
			while (Ps.hasNext()) {
				Triple t = (Triple) Ps.next();
				results.add(t);
			}
			return results;
		}
		// *, *, Object
		if (S == null && P == null) {
			Iterator Os = objects.get(O).iterator();
			while (Os.hasNext()) {
				Triple t = (Triple) Os.next();
				results.add(t);
			}
			return results;
		}
		// Subject, Predicate, *
		if (O == null) {
			Iterator Ses = subjects.get(S).iterator();
			while (Ses.hasNext()) {
				Triple t = (Triple) Ses.next();
				if(t.subject.equals(S) && t.predicate.equals(P)) {
					results.add(t);
				}
			}
			return results;
		}
		// Subject, *, Object
		if (P == null) {
			Iterator Ses = subjects.get(S).iterator();
			while (Ses.hasNext()) {
				Triple t = (Triple) Ses.next();
				if(t.subject.equals(S) && t.object.equals(O)) {
					results.add(t);
				}
			}
			return results;
		}
		// *, Predicate, Object
		if (S == null) {
			Iterator Ps = predicates.get(P).iterator();
			while (Ps.hasNext()) {
				Triple t = (Triple) Ps.next();
				if(t.predicate.equals(P) && t.object.equals(O)) {
				results.add(t);
				}
			}
			return results;
		}
		// Subject Predicate Object
		{
			Iterator Ses = subjects.get(S).iterator();
			while (Ses.hasNext()) {
				Triple t = (Triple) Ses.next();
				if(t.predicate.equals(P) && t.object.equals(O)) {
					results.add(t);
				}
			}
			return results;
		}
    }

	public Exp get(Symbol S, Symbol P) throws GenyrisException {
        List<Triple> found = new ArrayList();
		// Subject, Predicate, a single object value
		Iterator Ses = subjects.get(S).iterator();
		while (Ses.hasNext()) {
			Triple t = (Triple) Ses.next();
			if(t.predicate.equals(P)) {
				found.add(t);
			}
		}
		if(found.size() == 0) {
			throw new GenyrisException("More than one triple in graph matching " + S.toString() + " " + P.toString());
		}
		if(found.size() > 1) {
			throw new GenyrisException("More than one triple in graph matching " + S.toString() + " " + P.toString());
		}
		return found.get(0).object;
    }

	public Exp getList(Symbol S, Symbol P, Exp NIL) {
        Exp result = NIL;
		Iterator Ses = subjects.get(S).iterator();
		while (Ses.hasNext()) {
			Triple t = (Triple) Ses.next();
			if(t.predicate.equals(P)) {
				result = Pair.cons(t.object, result);
			}
		}
        return result;
    }

	public boolean empty() {
        return subjects.isEmpty();
    }

	public void remove(Symbol S, Symbol P, Exp O){
		Triple existing = contains(S, P, O);
		if (existing == null) {
			return;
		}
        subjects.remove(S, existing);
		predicates.remove(P,  existing);
		objects.remove(O,  existing);
    }

	public void remove(Triple t) {
		remove(t.subject, t.predicate, t.object);
    }

     public Exp asTripleList(Exp NIL) {
        Exp result = NIL;
        Collection items = subjects.values();
        Iterator iter = items.iterator();
        while(iter.hasNext()) {
            Triple item = (Triple)iter.next();
            result = new Pair(
						new Triple((Symbol)item.subject, (SimpleSymbol) item.predicate, item.object ),
						result);
        }
        return result;
    }

    public void put(Symbol S, Symbol P, Exp O) {
		Iterator iter = subjects.get(S).iterator();
		List<Triple> existing = new ArrayList();
        while (iter.hasNext()) {
            Triple t = (Triple)iter.next();
			if( P == t.predicate) {
				existing.add(t);
			}
		}
		// so as not to remove while iterating
		for (Triple t : existing) {
			remove(t);
		}
		add(new Triple(S, P, O));
    }


    public Exp subjects(Exp NIL) {
        Exp result = NIL;
        Iterator iter = subjects.values().iterator();
		Set<Symbol> Ses = new HashSet();
        while (iter.hasNext()) {
            Triple t = (Triple)iter.next();
			if( !Ses.contains(t.subject) ) {
				Ses.add((Symbol)t.subject);
            	result = new Pair(t.subject, result);
			}
        }
        return result;
    }
	    @Override
    public int compareTo(Object o) {
        return this == o ? 0 : 1;
    }

}

