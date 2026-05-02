// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp

import junit.framework.TestCase
import org.genyris.core.*
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.StandardEnvironment

class EnvironmentTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun testEnvDefine() {
        val interp = Interpreter()
        val env: Environment = StandardEnvironment(interp.getSymbolTable(), NilSymbol())
        val sym: Symbol = SimpleSymbol("answer")
        val `val` = Bignum(42)
        env.defineVariable(sym, `val`)
        assertEquals(`val`, env.lookupVariableValue(sym))
    }

    @kotlin.Throws(Exception::class)
    fun testEnvDefineQualified() {
        val interp = Interpreter()
        val env: Environment = StandardEnvironment(interp.getSymbolTable(), NilSymbol())
        val sym: Symbol = URISymbol("http://foo/bar#answer")
        val `val` = Bignum(42)
        env.defineVariable(sym, `val`)
        assertEquals(`val`, env.lookupVariableValue(sym))
    }

    @kotlin.Throws(Exception::class)
    fun testEnvNested() {
        val interp = Interpreter()
        val env1: Environment = StandardEnvironment(interp.getSymbolTable(), NilSymbol())
        val sym = SimpleSymbol("answer")
        val `val` = Bignum(42)
        env1.defineVariable(sym, `val`)
        assertEquals(`val`, env1.lookupVariableValue(sym))

        val env2: Environment = StandardEnvironment(env1)
        val val2 = Bignum(99)
        env2.defineVariable(sym, val2)

        assertEquals(val2, env2.lookupVariableValue(sym))
    }

    @kotlin.Throws(Exception::class)
    fun testEnvNestedSets() {
        val interp = Interpreter()
        val env1: Environment = StandardEnvironment(interp.getSymbolTable(), NilSymbol())
        val sym1 = SimpleSymbol("answer")
        val `val` = Bignum(42)
        env1.defineVariable(sym1, `val`)
        assertEquals(`val`, env1.lookupVariableValue(sym1))

        val env2: Environment = StandardEnvironment(env1)
        val sym2 = SimpleSymbol("question")
        val val2 = Bignum(99)
        env2.defineVariable(sym2, val2)
        assertEquals(val2, env2.lookupVariableValue(sym2))

        val val3 = Bignum(33)
        env1.setVariableValue(sym1, val3)
        assertEquals(val3, env1.lookupVariableValue(sym1))

        val val4 = Bignum(44)
        env2.setVariableValue(sym2, val4)
        assertEquals(val4, env2.lookupVariableValue(sym2))
    }

    @kotlin.Throws(Exception::class)
    fun testEnvEvalSelf() {
        val env: Environment = StandardEnvironment(null, NilSymbol())
        val int42 = Bignum(42)

        assertEquals(int42, int42.eval(env))
        val double4p2 = Bignum(4.2)
        assertEquals(double4p2, double4p2.eval(env))
    }

    @kotlin.Throws(Exception::class)
    fun testEnvEvalVariables() {
        val env: Environment = StandardEnvironment(null, NilSymbol())
        val answer = SimpleSymbol("answer")
        val int42 = Bignum(42)
        env.defineVariable(answer, int42)
        assertEquals(int42, int42.eval(env))
        assertEquals(int42, answer.eval(env))
    }
}
