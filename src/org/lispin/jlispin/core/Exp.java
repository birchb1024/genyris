package org.lispin.jlispin.core;

import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.MagicEnvironment;
import org.lispin.jlispin.interp.builtin.TagFunction;

public abstract class Exp implements Classifiable, Closure {

	public abstract Object getJavaValue();
	public abstract void acceptVisitor(Visitor guest);
		
	public Exp[] computeArguments(Environment ignored, Exp exp) throws LispinException {
		Exp[] args = {exp};
		return args;
	}

	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		if(arguments[0] == environment.getNil()) {
			throw new LispinException("Empty body to exp invocation does not make sense." + this.toString());
		}
		Environment newEnv = new MagicEnvironment(environment, this);
        if(arguments[0].listp()) {
            return Evaluator.evalSequence(newEnv, arguments[0]);		            
        }
        else {
            try {
                Lobject klass = (Lobject) Evaluator.eval(newEnv, arguments[0]);  
                // call validator if it exists
                TagFunction.validateClassTagging(environment, this, klass);
                this.addClass(klass);
                return this;
            }
            catch (ClassCastException e) {
                throw new LispinException("type tag failure: " + arguments[0] + " is not a class");
            }
        }
	}
	
	public boolean isNil() {
		return false;
	}

    public Exp car() throws AccessException {
		throw new AccessException("attempt to take car of non-cons");
	}
	
	public Exp cdr() throws AccessException {
		throw new AccessException("attempt to take cdr of non-cons");
	}
	
	public Exp setCar(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}
	
	public Exp setCdr(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}

	public int hashCode() {
		return getJavaValue().hashCode();
	}

	public boolean equals(Object compare) {
		if (compare.getClass() != this.getClass())
			return false;
		else
			return this.getJavaValue().equals(((Exp) compare).getJavaValue());
	}

	public abstract String toString();


	public boolean listp() {
		return (this.getClass() == Lcons.class);
	}

	public boolean isSelfEvaluating() {
		return true;
	}

	public int length(Lsymbol NIL) throws AccessException {
		Exp tmp = this;
		int count = 0;

		while (tmp != NIL) {
			tmp = tmp.cdr();
			count++;
		}
		return count;
	}

	public Exp nth(int number, Lsymbol NIL) throws AccessException {
		if (this == NIL) {
			throw new AccessException("nth called on nil.");
		}
		Exp tmp = this;
		int count = 0;
		while (tmp != NIL) {
			if (count == number) {
				return tmp.car();
			}
			tmp = tmp.cdr();
			count++;
		}
		throw new AccessException("nth could not find item: " + number);
	}

	public Exp last(Lsymbol NIL) throws AccessException {
		if (this == NIL)
			return NIL;
		Exp tmp = this;
		while (tmp.cdr() != NIL) {
			tmp = tmp.cdr();
		}
		return tmp.car();
	}

}
