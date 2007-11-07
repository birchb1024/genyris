package org.genyris.core;

public class LconsWithcolons extends Lcons {

    public LconsWithcolons(Exp car, Exp cdr) {
        super(car, cdr);
    }
    public String getBuiltinClassName() {
        return Constants.PRINTWITHCOLON;
    }
}
