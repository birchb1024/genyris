package org.genyris.dl;

import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;

public class Triple extends ExpWithEmbeddedClasses {
	
	public Exp subject;
	public Symbol predicate;
	public Exp object;

	public Triple(Exp subject, Symbol predicate, Exp object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitTriple(this);

	}

	public Object getJavaValue() {
		return null;
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
