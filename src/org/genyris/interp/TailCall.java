package org.genyris.interp;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;

public class TailCall extends Atom {


    public Exp[] arguments;
    public Closure proc;

    public TailCall(Closure proc, Exp[] arguments) {
       this.arguments = arguments;
       this.proc = proc;
    }

    @Override
    public String toString() {
        String repr = "TailCall " + proc.toString() + " ";
        for( int i=0; i< arguments.length;  i++) {
            repr += arguments[i].toString() + " ";
        }
        return repr;
    }

    @Override
    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitTailCall(this);     
    }

    @Override
    public Exp eval(Environment env) throws GenyrisException {
        return this;
    }

    @Override
    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.TAILCALL();
    }

}
