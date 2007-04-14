package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public abstract class BuiltInProcedure extends Exp {
	
	public abstract Exp apply(Environment env,  Exp[] arguments);

}
