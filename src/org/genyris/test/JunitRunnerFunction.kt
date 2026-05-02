//     Copyright 2008 Peter William Birch <birchb@genyis.org>
//
//     This software may be used and distributed according to the terms
//     of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test

import junit.textui.TestRunner
import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class JunitRunnerFunction(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_SYSTEM, "junit-test-runner", "sys"), true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>?,
        envForBindOperations: Environment?
    ): Exp {
        val suite = AllTestSuite.makeSuite()
        val result = TestRunner.run(suite)

        return Pair(
            Bignum(result.errorCount() + result.failureCount()),
            Bignum(suite.countTestCases())
        )
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(JunitRunnerFunction(interpreter))
        }
    }
}
