package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public abstract class Applicable {
	
	public abstract Exp apply(Procedure proc, Environment env,  Exp[] arguments) throws Exception;

}
