package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public class ClosureFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envToCapture) throws LispinException {
		return new CallableEnvironment(envToCapture);
	}

}
