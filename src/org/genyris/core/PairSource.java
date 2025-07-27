package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.PairSourceEnvironment;


public class PairSource extends Pair {
    public String filename;
    public int lineNumber;

    public PairSource(Exp car, Exp cdr, String filename, int lineNumber) {
        super(car, cdr);
        this.lineNumber = lineNumber;
        this.filename = filename;
    }
    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.PAIRSOURCE();
    }
    public Environment makeEnvironment(Environment parent) throws GenyrisException {
        return new PairSourceEnvironment(parent, this);
    }
    public Exp dir(Internable table) {
        return Pair.cons2(new DynamicSymbol(table.LINENUMBER()),
                new DynamicSymbol(table.FILENAME()), 
                super.dir(table));
    }
    public static PairSource clone(PairSource l, Exp r) {
        return new PairSource(l, r, l.filename, l.lineNumber);
    }

    public Exp eval(Environment env) throws GenyrisException {
        try {
            return super.eval(env);
        }
        catch (GenyrisException e) {
            if (e.filename == null ) {
                e.filename = this.filename;
                e.lineNumber = this.lineNumber;
            }
            throw e;
        }
    }
}
