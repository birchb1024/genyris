package org.genyris.dl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;

public class TripleSet extends ExpWithEmbeddedClasses {
	
	private Set triples;
	
	public TripleSet() {
		triples = new HashSet();
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitTripleSet(this);
	}

	public Iterator iterator() {
		return triples.iterator();
	}
	public Object getJavaValue() {
		return null;
	}

	public String toString() {
		return "(tripleset"  + ")";
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
        }
        else {
        	return false;
        }
    }
    
    public void add(Triple t) {
    	triples.add(t);
    }

    public TripleSet query(Exp subject, Exp predicate, Exp Object) {
    	TripleSet results = new TripleSet();
    	Iterator iter = triples.iterator();
    	while(iter.hasNext()) {
    		Triple t = (Triple)iter.next();
    		if(subject != null) {
    			if(t.subject == subject) {
    				results.add(t);
    			}   			
    		}
    	}
    	return results;
    }

	public boolean empty() {
		return triples.isEmpty();
	}
}
