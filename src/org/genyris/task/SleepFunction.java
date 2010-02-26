// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.task;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.exception.GenyrisInterruptedException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class SleepFunction extends TaskFunction {

	public SleepFunction(Interpreter interp) {
		super(interp, "sleep", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 1);
		try {
			Thread.sleep(((Bignum)arguments[0]).bigDecimalValue().longValue());
		} catch (InterruptedException e) {
			throw new GenyrisInterruptedException(e.getMessage());
		}
        return NIL;
	}
	

}