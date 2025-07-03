package org.genyris.dl;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class Triple extends Atom implements Comparable {

    public final Symbol subject;
    public final Symbol predicate;
    public final Exp object;

    public Triple(Symbol subject, Symbol predicate, Exp object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitTriple(this);

    }
    
	public Environment makeEnvironment(Environment parent) throws GenyrisException {
		return new TripleEnvironment(parent, this);
	}


    public String toString() {
        return "(triple " + subject + " " + predicate +  " " + object + ")";
    }

    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.TRIPLE();
    }

    public int hashCode() {
        return subject.hashCode() + predicate.hashCode() + object.hashCode();
    }

    public boolean equals(Object compare) {
        if (compare instanceof Triple) {
            Triple t = (Triple) compare;
            return subject == t.subject && predicate == t.predicate
                && object.equals(t.object);
        }
        else {
            return false;
        }
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public int compareTo(Object arg0) {
        return this.toString().compareTo(((Triple)arg0).toString()); // #TODO use the Triples fields
	}
	
	public Exp dir(Internable table) {
		return Pair.cons3(table.SUBJECT(), 
				table.PREDICATE(), 
				table.OBJECT(), 
				Pair.cons2(table.VARS(), table.CLASSES(), table.NIL()));
	}


}
