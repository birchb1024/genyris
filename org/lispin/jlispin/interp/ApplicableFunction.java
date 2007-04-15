package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public abstract class ApplicableFunction {
	
	public abstract Exp apply(Procedure proc, Environment env,  Exp[] arguments) throws LispinException;

}
