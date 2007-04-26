package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;

public abstract class Procedure extends Exp {
	
	Environment _env;
	Exp _lambdaExpression;
	final ApplicableFunction _functionToApply;
	protected int _numberOfRequiredArguments;

	public Procedure(Environment environment, Exp expression, ApplicableFunction appl) throws LispinException {
		_env = environment;
		_lambdaExpression = expression;
		_functionToApply = appl;
		_numberOfRequiredArguments = -1;
	}
	
	private int countFormalArguments(Exp exp) throws AccessException {
		int count = 0;
		while( exp != SymbolTable.NIL ) {
			if( ((Lcons)exp).car() == SymbolTable.REST ) {
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
			return SymbolTable.NIL; // ignore extra arguments 
		else
			return _lambdaExpression.cdr().car().nth(index);
	}

	public Object getJavaValue() {
		return "<" + _functionToApply.getName() + ">";
	}

	public Exp getBody() throws AccessException {
		return _lambdaExpression.cdr().cdr();
	}
	
	public abstract Exp[] computeArguments(Environment env, Exp exp) throws LispinException;

	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
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
		
		return args.last();

	}
	
}