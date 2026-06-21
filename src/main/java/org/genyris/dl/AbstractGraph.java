package org.genyris.dl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.genyris.core.*;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.DynamicEnvironment;
import org.genyris.interp.Environment;
import org.genyris.interp.PairEnvironment;

public abstract class AbstractGraph extends Atom {

    public AbstractGraph() {
        super();
    }

    public abstract Exp subjects(Exp NIL);

    public abstract Exp asTripleList(Exp NIL);

    public abstract void remove(Triple triple);

    public abstract boolean empty();

    public abstract Exp getList(Symbol subject, Symbol predicate, Exp NIL);

    public abstract Exp get(Symbol subject, Symbol predicate) throws GenyrisException;

    public abstract AbstractGraph select(Symbol subject, Symbol predicate, Exp object, Closure condition,
            Environment env) throws GenyrisException;

    public abstract void add(Triple t);

    public abstract void remove(Symbol subject, Symbol predicate, Exp object);

    public abstract Triple contains(Symbol subject, Symbol predicate, Exp object);

    public abstract int length();

    public abstract Symbol getBuiltinClassSymbol(Internable table);

    public abstract String toString();

    public abstract Iterator iterator();

    public abstract Exp predicates(Symbol subject, Exp NIL);

    public abstract void put(Symbol subject, Symbol predicate, Exp object);

    public boolean equals(Object compare) {
        if (this == compare) {
            return true; 
        }
        if (!(compare instanceof AbstractGraph)) {
            return false;
        }
        return this.toString().equals(((AbstractGraph)compare).toString()); // TODO is this inefficient ?
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
    	guest.visitGraph(this);
    }

   public AbstractGraph difference(AbstractGraph toRemove) throws GenyrisException { return this ; }

  /* #TODO this code is bogus:
    	AbstractGraph result =  new GraphHashSimple();
    	Iterator iter = iterator();
    	while(iter.hasNext()) {
    		Triple item = (Triple)iter.next();
    		remove(item.subject, item.predicate, item.object);
    	}
    	return result;
    }*/

    public AbstractGraph union(AbstractGraph other) throws GenyrisException {
        AbstractGraph result =  new GraphHashSimple();
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

    public abstract ArrayList<Triple> toSortedTriplesArray();

    public Environment makeEnvironment(Environment parent) throws GenyrisException {
        return new GraphEnvironment(parent, this);
    }

    public Exp shift(Symbol sub, Environment env) throws GenyrisException {
        Symbol NIL = env.getNil();
        AbstractGraph g;
        GraphHashSimple result = new GraphHashSimple();
        Exp subjects = this.subjects(NIL);
        // an empty graph, return nil
        if (subjects == NIL ) {
            return NIL;
        }
        if (!(subjects instanceof Pair) ) {
            throw new GenyrisException("subjects are not a list! This should never happen.");
        }
        // a graph with just one subject, NIL, return a List of values
        Pair subs = (Pair)subjects;
        if( subs.length(NIL) == 1 && subs.car().equals(NIL) ) {
            return this.getList(NIL, sub, NIL);
        }
        // strip out all the other triples, leave the ones with this subect, and set subjects to NIL
        g = this.select(sub, null, null, null, env);
        Iterator iter = g.iterator();
        while(iter.hasNext()) {
            Triple item = (Triple)iter.next();
            result.add(new Triple(NIL, item.predicate, item.object));
        }
        return result;
    }
}