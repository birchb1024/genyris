package org.genyris.dl;

import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;

public class Triple extends ExpWithEmbeddedClasses {
	
	public final Exp subject;
	public final Symbol predicate;
	public final Exp object;

	public Triple(Exp subject, Symbol predicate, Exp object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public static Triple mkTripleFromList(Exp exp) throws GenyrisException {
		Exp subject = exp.car();
		Exp predicate = exp.cdr().car();
		Exp object = exp.cdr().cdr().car();
		if (!(predicate instanceof Symbol)) {
			throw new GenyrisException("mkTripleFromList was expecting a Symbol predicate, got: " + predicate);
		}
		return new Triple(subject, (Symbol) predicate, object);
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitTriple(this);

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
        	return subject == t.subject 
        		&& predicate == t.predicate
        		&& object == t.object;
        }
        else {
        	return false;
        }
    }

}
