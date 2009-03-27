package org.genyris.dl;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class Triple extends Atom {

    public final Exp subject;
    public final SimpleSymbol predicate;
    public final Exp object;

    public Triple(Exp subject, SimpleSymbol predicate, Exp object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public static Triple mkTripleFromList(Exp exp) throws GenyrisException {
        Exp subject = exp.car();
        Exp predicate = exp.cdr().car();
        Exp object = exp.cdr().cdr().car();
        assertIsSymbol(predicate, "mkTripleFromList was expecting a Symbol predicate, got: ");
        return new Triple(subject, (SimpleSymbol) predicate, object);
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
            return subject.equals(t.subject)
                && predicate == t.predicate
                && object.equals(t.object);
        }
        else {
            return false;
        }
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

}
