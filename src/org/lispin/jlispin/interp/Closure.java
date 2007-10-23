package org.lispin.jlispin.interp;

import org.genyris.core.Exp;

public interface Closure {

	public abstract Exp[] computeArguments(Environment env, Exp exp) throws LispinException;

	public abstract Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException;

}