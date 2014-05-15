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

    public abstract Exp getList(Exp subject, Symbol predicate, Exp NIL);

    public abstract Exp get(Exp subject, Exp predicate) throws GenyrisException;

    public abstract AbstractGraph select(Exp subject, Exp predicate, Exp object, Closure condition,
            Environment env) throws GenyrisException;

    public abstract void add(Triple t);

    public abstract void remove(Exp subject, Exp predicate, Exp object);

    public abstract boolean contains(Exp subject, Exp predicate, Exp object);

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
    			if(otherTS.contains(item.subject, item.predicate, item.object)) {
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

    public GraphList difference(GraphList toRemove) throws GenyrisException {
    	GraphList result =  new GraphList();
    	Iterator iter = iterator();
    	while(iter.hasNext()) {
    		Triple item = (Triple)iter.next();
    		remove(item.subject, item.predicate, item.object);
    	}
    	return result;
    }

    public GraphList union(AbstractGraph other) throws GenyrisException {
        GraphList result =  new GraphList();
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
    			remove(rec);
    		}
    	}
    	add(new Triple(subject, predicate, object));
    }

}