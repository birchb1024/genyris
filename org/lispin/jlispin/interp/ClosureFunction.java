package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public class ClosureFunction extends ApplicableFunction {

	public Exp bindAndExecute(AbstractClosure proc, Exp[] arguments, Environment envToCapture) throws LispinException {
		return envToCapture;
	}

}
