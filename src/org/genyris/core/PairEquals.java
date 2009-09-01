package org.genyris.core;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class PairEquals extends Pair {

    public PairEquals(Exp car, Exp cdr) {
        super(car, cdr);
    }
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.PAIREQUAL();
	}
	public Exp eval(Environment env) throws GenyrisException {
		Exp value = cdr().eval(env);
		if(car() instanceof Symbol) {
			env.setVariableValue((Symbol)car(), value);			
		} else {
			throw new GenyrisException("Cannot assign " + value + " to non-Symbol " + car());
		}
		return value;
	} 

}
