package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

public class Evaluator {

	public static Exp eval(Environment env, Exp expression) throws UnboundException, AccessException, LispinException {
		if( expression.isSelfEvaluating()) {
			return expression; }
		else if( expression.getClass() == Lsymbol.class) {
			return env.lookupVariableValue(expression);
		}
		else if( expression.listp() ) { 
			Closure proc = (Closure) eval(env, expression.car()); // TODO dynamic check.
			Exp[] arguments = proc.computeArguments(env, expression.cdr());
			return proc.applyFunction(env, arguments );
		}
		else 
			return SymbolTable.NIL;
	}

	public static Exp evalSequence(Environment env, Exp body) throws LispinException {
		if( body.cdr() == SymbolTable.NIL) {
			return eval(env, body.car());
		}
		else {
			eval(env, body.car());
			return evalSequence(env, body.cdr());
		}
	}

}