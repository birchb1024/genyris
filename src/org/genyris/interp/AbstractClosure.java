package org.genyris.interp;

import org.genyris.core.AccessException;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lcons;
import org.genyris.core.Lsymbol;

public abstract class AbstractClosure extends ExpWithEmbeddedClasses implements Closure {

	Environment _env;
	Exp _lambdaExpression;
	final ApplicableFunction _functionToApply;
	protected int _numberOfRequiredArguments;
	Lsymbol NIL, REST;

	public AbstractClosure(Environment environment, Exp expression, ApplicableFunction appl) throws GenyrisException {
		_env = environment;
		_lambdaExpression = expression;
		_functionToApply = appl;
		_numberOfRequiredArguments = -1;
		NIL = environment.getNil();
		REST = environment.getInterpreter().getSymbolTable().internString(Constants.REST); // TOD performance
	}

	private int countFormalArguments(Exp exp) throws AccessException {
		int count = 0;
		while( exp != NIL ) {
			if( ((Lcons)exp).car() == REST ) {
				count += 1;
				break;
			}
			count += 1;
			exp = exp.cdr();
		}
		return count;
	}

	public Exp getArgumentOrNIL(int index) throws AccessException {
		if( getNumberOfRequiredArguments() <= index)
			return NIL; // ignore extra arguments
		else
			return _lambdaExpression.cdr().car().nth(index, NIL);
	}

	public Object getJavaValue() {
		return "<" + _functionToApply.getName() + ">";
	}

	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}

	public abstract Exp[] computeArguments(Environment env, Exp exp) throws GenyrisException;

	public Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException {
		return _functionToApply.bindAndExecute(this, arguments, environment); // double dispatch
	}

	public Environment getEnv() {
		return _env;
	}

	public int getNumberOfRequiredArguments() throws AccessException {
		if( _numberOfRequiredArguments < 0 ) {
			_numberOfRequiredArguments = countFormalArguments(_lambdaExpression.cdr().car());
		}
		return _numberOfRequiredArguments;
	}

	public String getName() {
		return _functionToApply.getName();
	}

	public Exp getLastArgumentOrNIL(int i) throws AccessException {
		Exp args = _lambdaExpression.cdr().car();

		return args.last(NIL);

	}

}