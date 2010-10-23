//     Copyright 2008 Peter William Birch <birchb@genyis.org>
//
//     This software may be used and distributed according to the terms
//     of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test;

import junit.framework.Test;
import junit.framework.TestResult;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class JunitRunnerFunction extends ApplicableFunction {

    public JunitRunnerFunction(Interpreter interp) {
        super(interp, "self-test-runner", true);
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments,
            Environment envForBindOperations) throws GenyrisException {
    	Test suite = org.genyris.test.AllTestSuite.makeSuite();
    	TestResult result = junit.textui.TestRunner.run(suite);
    	
     	return new Pair(new Bignum(result.errorCount()+ result.failureCount()),
     			new Bignum(suite.countTestCases()));
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new JunitRunnerFunction(interpreter));
    }
}
