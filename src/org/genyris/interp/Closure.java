package org.genyris.interp;

import org.genyris.core.Exp;

public interface Closure {

	public abstract Exp[] computeArguments(Environment env, Exp exp) throws GenyrisException;

	public abstract Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException;

}