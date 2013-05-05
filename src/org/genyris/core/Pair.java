// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.io.StringWriter;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.format.AbstractFormatter;
import org.genyris.format.BasicFormatter;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.PairEnvironment;
import org.genyris.interp.UnboundException;

public class Pair extends ExpWithEmbeddedClasses {

    private  Exp _car;
    private  Exp _cdr;

    public Pair(Exp car, Exp cdr) {
        _car = car;
        _cdr = cdr;
    }
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.PAIR();
	}

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitPair(this);
    }

    public boolean equals(Object compare) {
        if (!(compare instanceof Pair))
            return false;
        else
            return this._car.equals(((Pair)compare)._car)
                && this._cdr.equals(((Pair)compare)._cdr);
    }

	public boolean isPair() {
		return true;
	}

	public Exp car() {
        return _car;
    }

    public Exp cdr() {
        return _cdr;
    }

    public Exp setCar(Exp exp) {
        this._car = exp;;
        return this;
    }

    public Exp setCdr(Exp exp) {
        this._cdr = exp;;
        return this;
    }

    public String toString() {
        StringWriter buffer = new StringWriter();
        AbstractFormatter formatter = new BasicFormatter(buffer);
        try {
			this.acceptVisitor(formatter);
		} catch (GenyrisException e) {
			return e.getMessage();
		}
        return buffer.toString();
    }
	public int hashCode() {
		return _car.hashCode() + _cdr.hashCode();
	}
	public Exp eval(Environment env) throws GenyrisException {
		Closure proc = null;
		Exp[] arguments = null;
		try {
        	proc = (Closure) car().eval(env);
        } catch (UnboundException e1) {
        	try {
        		proc = env.getSymbolTable().PROCEDUREMISSING().lookupVariableValue(env);
        	} catch (UnboundException e2) {
        		throw e1;
        	}
            arguments = prependArgument(car(), proc.computeArguments(env, cdr()));
            Exp retval = proc.applyFunction(env, arguments);
            if(proc.isBiscuit()) {
                retval = retval.eval(env);
            }
            return retval;      
        }
        arguments = proc.computeArguments(env, cdr());
        Exp retval = proc.applyFunction(env, arguments);
        if(proc.isBiscuit()) {
            retval = retval.eval(env);
        }
        return retval;      

	}
	private Exp[] prependArgument( Exp firstArg, Exp[] tmparguments)
			throws GenyrisException {
		Exp[] arguments = new Exp[tmparguments.length+1];
		arguments[0] = firstArg;
		for(int i=0;i<tmparguments.length;i++){
			arguments[i+1] = tmparguments[i];
		}
		return arguments;
	} 
	
    public Exp evalSequence(Environment env) throws GenyrisException {
        SimpleSymbol NIL = env.getNil();
        Exp body = this;
        if (body.cdr() == NIL) {
            return body.car().eval(env);
        }
        else {
            body.car().eval(env);
            return body.cdr().evalSequence(env);
        }
    }
	public int length(Symbol NIL) throws AccessException {
		Exp tmp = this;
		int count = 0;

		while (tmp != NIL && (tmp instanceof Pair)) {
			tmp = tmp.cdr();
			count++;
		}
		return count;
	}
	public Exp nth(int number, Symbol NIL) throws AccessException {
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
	public Environment makeEnvironment(Environment parent) throws GenyrisException {
		return new PairEnvironment(parent, this);
	}
	public static Exp reverse(Exp list, Exp NIL) throws GenyrisException {
		if(list.isNil()) {
			return list;
		}
		if(list instanceof Pair) {
			Exp rev_result = NIL;

			while (list != NIL) {
				rev_result = new Pair(list.car(), rev_result);
				list = list.cdr();
			}
			return (rev_result);
		} else {
			throw new GenyrisException("reverse: not a list: " + list);
		}
	}
	
	public static Exp cons(Exp a, Exp b) {
		return new Pair(a, b);
	}

	public static Exp cons2(Exp a, Exp b, Exp NIL) {
		return new Pair(a, new Pair(b, NIL));
	}

	public static Exp cons3(Exp a, Exp b, Exp c, Exp NIL) {
		return new Pair(a, new Pair(b, new Pair(c, NIL)));
	}

	public static Exp cons4(Exp a, Exp b, Exp c, Exp d, Exp NIL) {
		return new Pair(a, new Pair(b, new Pair(c, new Pair(d, NIL))));
	}

	public Exp dir(Internable table) {
		return Pair.cons2(new DynamicSymbol(table.LEFT()), 
		        new DynamicSymbol(table.RIGHT()),super.dir(table));
	}

}
