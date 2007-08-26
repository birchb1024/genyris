package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

public abstract class AbstractClosure extends Exp implements Closure {
	
	Environment _env;
	Exp _lambdaExpression;
	final ApplicableFunction _functionToApply;
	protected int _numberOfRequiredArguments;
	Lsymbol NIL;

	public AbstractClosure(Environment environment, Exp expression, ApplicableFunction appl) throws LispinException {
		_env = environment;
		_lambdaExpression = expression;
		_functionToApply = appl;
		_numberOfRequiredArguments = -1;
		NIL = environment.getNil();
	}
	
	private int countFormalArguments(Exp exp) throws AccessException {
		int count = 0;
		while( exp != NIL ) {
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
		
		return args.last(NIL);

	}
    public void addClass(Exp klass) {
        ; // Noop
    }

    public Exp getClasses(Lsymbol nil) {
        // TODO Auto-generated method stub
        return NIL;
    }

    public void removeClass(Exp klass) {
        ; // Noop
    }
    public boolean isTaggedWith(Lobject klass) {
        return false;
    }
    public boolean isInstanceOf(Lobject klass) {
        return false;
    }

}