package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;

public class Evaluator {
	
	// TODO - refactor this to use visitor in Exp and gt rid of if()s

	public static Exp eval(Environment env, Exp expression) throws UnboundException, AccessException, LispinException {
		Lsymbol NIL = env.getNil();
		if( expression.isSelfEvaluating()) {
			return expression; }
		else if( expression.getClass() == Lsymbol.class) {
			return env.lookupVariableValue(expression);
		}
		else if( expression.listp() ) { 
			try {
				Closure proc = (Closure) eval(env, expression.car());
				Exp[] arguments = proc.computeArguments(env, expression.cdr());
				return proc.applyFunction(env, arguments );
			}
			catch (ClassCastException e) {
				throw new LispinException("Attempt to call something which is not callable." + expression.toString());
			}
		}
		else 
			return NIL;
	}

	public static Exp evalSequence(Environment env, Exp body) throws LispinException {
		Lsymbol NIL = env.getNil();
		if(body == NIL) {
			return NIL;
		}
		if( body.cdr() == NIL) {
			return eval(env, body.car());
		}
		else {
			eval(env, body.car());
			return evalSequence(env, body.cdr());
		}
	}


}