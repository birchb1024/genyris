package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;

public interface Closure {

	public abstract Exp getArgumentOrNIL(int index) throws AccessException;

	public abstract Object getJavaValue();

	public abstract Exp getBody() throws AccessException;

	public abstract Exp[] computeArguments(Environment env, Exp exp)
			throws LispinException;

	public abstract Exp applyFunction(Environment environment, Exp[] arguments)
			throws LispinException;

	public abstract Environment getEnv();

	public abstract int getNumberOfRequiredArguments() throws AccessException;

	public abstract String getName();

	public abstract Exp getLastArgumentOrNIL(int i) throws AccessException;

}