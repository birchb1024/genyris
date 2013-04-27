package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.PairSourceEnvironment;


public class PairSource extends Pair {
    public String filename;
    public int lineNumber;


    public PairSource(Exp car, Exp cdr, String filename, int lineNUmber) {
        super(car, cdr);
        this.lineNumber = lineNUmber;
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

}
