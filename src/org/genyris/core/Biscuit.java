package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class Biscuit extends Atom {


    private Exp expression;

    public Biscuit(Exp expression) {
       this.expression = expression;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getName() + " " + expression.toString() + ">";
    }

    @Override
    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitBiscuit(this);  
    }

    @Override
    public Exp eval(Environment env) throws GenyrisException {
        return this;
    }

    @Override
    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.BISCUIT();
    }

    public Exp getExpression() {
        return expression;
    }


}
