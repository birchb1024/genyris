package org.genyris.dl;

import java.util.Iterator;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;

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

    public abstract int hashCode();

    public abstract Symbol getBuiltinClassSymbol(Internable table);

    public abstract String toString();

    public abstract Iterator iterator();

    public boolean equals(Object compare) {
    	if (!(compare instanceof AbstractGraph)) {
    		return false;
    	} else {
    		int matches = 0;
    		AbstractGraph otherTS = (AbstractGraph) compare;
    		Iterator iter = iterator();
    		while (iter.hasNext()) {
    			Triple item = (Triple)iter.next();
    			if(otherTS.contains(item.subject, item.predicate, item.object) != null) {
    				matches++;
    			}
    		}
    		if ( matches == length() && matches == otherTS.length()) {
    			return true;
    		}
    		return false;
    	}
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

    public void put(Symbol subject, Symbol predicate, Exp object) {
    	Iterator iter = iterator();
    	while(iter.hasNext()) {
    		Triple rec = (Triple) iter.next();
    		if(rec.subject == subject
    				&& rec.predicate == predicate) {
    			remove(rec);
    		}
    	}
    	add(new Triple(subject, predicate, object));
    }

}