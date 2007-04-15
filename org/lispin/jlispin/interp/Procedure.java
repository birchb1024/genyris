package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;

public interface Procedure {

	public abstract Exp getArgument(int i) throws AccessException;

	public abstract Exp getBody() throws AccessException;

	public abstract Exp[] computeArguments(Environment environment, Exp exp) throws Exception;

	public abstract Applicable getApplyStyle();
	
}