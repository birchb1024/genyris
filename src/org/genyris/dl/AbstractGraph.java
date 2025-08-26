package org.genyris.dl;

import java.util.Iterator;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

public abstract class AbstractGraph extends Atom {

    public AbstractGraph() {
        super();
    }

    public abstract Exp subjects(Exp NIL) throws AccessException;

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
    		return this == compare;
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
    	guest.visitGraph(this);
    }

   public AbstractGraph difference(AbstractGraph toRemove) throws GenyrisException { return this ; }
/*
   #TODO this code is bogus:
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
}