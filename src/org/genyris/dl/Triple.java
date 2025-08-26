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
        if (!(compare instanceof Triple)) {
            return false;
        }
        Triple t = (Triple) compare;
        return subject.equals(t.subject) && predicate.equals(t.predicate) && object.equals(t.object);
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public int compareTo(Object O) {
        if(!(O instanceof Triple)){
            return -1;
        }
        Triple other = (Triple) O;
        int comparison = subject.compareTo(other.subject);
        if(comparison != 0) {
            return comparison;
        }
        comparison = predicate.compareTo(other.predicate);
        if(comparison != 0) {
            return comparison;
        }
        return object.compareTo(other.object);
	}
	
	public Exp dir(Internable table) {
		return Pair.cons3(table.SUBJECT(), 
				table.PREDICATE(), 
				table.OBJECT(), 
				Pair.cons2(table.VARS(), table.CLASSES(), table.NIL()));
	}


}
