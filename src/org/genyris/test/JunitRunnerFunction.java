//     Copyright 2008 Peter William Birch <birchb@genyis.org>
//
//     This software may be used and distributed according to the terms
//     of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class JunitRunnerFunction extends ApplicableFunction {

	public JunitRunnerFunction(Interpreter interp) {
		super(interp, "self-test-runner", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		junit.textui.TestRunner.run(org.genyris.test.AllTestSuite.makeSuite());
		return NIL;
	}
}
