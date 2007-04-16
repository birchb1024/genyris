package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public abstract class ApplicableFunction {
	
	public abstract Exp bindAndExecute(Procedure proc, Exp[] arguments, Environment envForBindOperations) throws LispinException;

	public String getName() {
		return this.getClass().getName();
	}

}
