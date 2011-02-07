package org.genyris.core;


public class PairEquals extends Pair {

    public PairEquals(Exp car, Exp cdr) {
        super(car, cdr);
    }
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.PAIREQUAL();
	}

}
