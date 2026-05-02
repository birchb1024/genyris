// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.core.Bignum
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.format.BasicFormatter
import org.genyris.format.Formatter
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.StandardEnvironment
import org.genyris.io.InStream
import org.genyris.io.Parser
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import java.io.StringWriter

class EvalApplyTest : TestCase() {
    @kotlin.Throws(Exception::class)
    fun testLambda1() {
        val interp = Interpreter()
        val env = interp.getGlobalEnv()
        val table = interp.getSymbolTable()
        val input: InStream = UngettableInStream(
            StringInStream(
                "((lambda (x) (cons x x)) 23)"
            )
        )
        val parser = Parser(table, input)
        val expression = parser.read()
        val result = expression.eval(env)
        val out = StringWriter()
        val formatter: Formatter = BasicFormatter(out)
        result.acceptVisitor(formatter)
        Assert.assertEquals("(23 = 23)", out.getBuffer().toString())
    }

    @kotlin.jvm.JvmOverloads
    @kotlin.Throws(Exception::class)
    fun excerciseEval(exp: String, expected: String?, exceptionExpected: String? = "") {
        val interp = Interpreter()
        val env = interp.getGlobalEnv()
        val env2: Environment = StandardEnvironment(env)
        env2.defineVariable(interp.intern("alpha"), Bignum(23))
        env2.defineVariable(interp.intern("bravo"), Bignum(45))
        val input: InStream = UngettableInStream(StringInStream(exp))
        val parser = Parser(interp.getSymbolTable(), input)
        val expression = parser.read()

        try {
            val result: Exp
            result = expression.eval(env2)

            val out = StringWriter()
            val formatter: Formatter = BasicFormatter(out)
            result.acceptVisitor(formatter)
            Assert.assertEquals(expected, out.getBuffer().toString())
        } catch (e: GenyrisException) {
            Assert.assertEquals(exceptionExpected, e.getMessage())
        }
    }

    @kotlin.Throws(Exception::class)
    fun testLambdaVariables() {
        excerciseEval("alpha", "23")
        excerciseEval("bravo", "45")
        excerciseEval("(quote alpha)", "alpha")
        excerciseEval("(cons alpha bravo)", "(23 = 45)")
        excerciseEval("(cons alpha (cons bravo nil))", "(23 45)")
    }

    @kotlin.Throws(Exception::class)
    fun testLambdaBuitins() {
        excerciseEval("(cons 4 5)", "(4 = 5)")
        excerciseEval("(cons 4 (cons 5 nil))", "(4 5)")
        excerciseEval("(cons 4 nil)", "(4)")
        excerciseEval("(quote 99)", "99")
        excerciseEval("(quote (34)))", "(34)")
        excerciseEval("(quote (foo)))", "(foo)")
        excerciseEval("(car (quote (1 = 2)))", "1")
        excerciseEval("(cdr (quote (1 = 2)))", "2")
        excerciseEval("(rplaca (quote (1 = 2)) 99)", "(99 = 2)")
        excerciseEval("(rplacd (quote (1 = 2)) 100)", "(1 = 100)")
        excerciseEval("(quote (foo)))", "(foo)")
    }

    @kotlin.Throws(Exception::class)
    fun testTooFewLambdaArguments() {
        excerciseEval("((lambda () (cons 44 44)))", "(44 = 44)")
        excerciseEval(
            "((lambda (x y) (cons x x)) 23)", null,
            "Too few arguments supplied to proc: anonymous lambda. Args were: (23 )"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testTooManyLambdaArguments() {
        excerciseEval(
            "((lambda (x) (cons x x)) 23 24 25)", "(24 = 23)",
            "Too many arguments supplied to proc: anonymous lambda. Args were: (23 24 25 )"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testLambda2() {
        excerciseEval("((lambda () (cons 44 44)))", "(44 = 44)")
        excerciseEval("((lambda (x) (cons x x)) 23)", "(23 = 23)")
        excerciseEval("((lambda (x y) (cons x y)) 5 nil)", "(5)")
        excerciseEval("((lambda (x y) (quote x)) 23 45)", "x")
    }

    @kotlin.Throws(Exception::class)
    fun testLambdaq1() {
        excerciseEval("((lambdaq (s) (cons 1 s)) foo)", "(1 = foo)")
        excerciseEval(
            "((lambdaq (s) (cons 1 s)) (cons foo bar))",
            "(1 cons foo bar)"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testSequence() {
        excerciseEval("((lambda (x) (cons x x) (cons 3 4)) 23)", "(3 = 4)")
        excerciseEval(
            "((lambda (x y) (cons x x) (cons y x)) 23 45)",
            "(45 = 23)"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testDefine() {
        excerciseEval("((lambda () (defvar (quote charlie) 99) charlie))", "99")
    }

    @kotlin.Throws(Exception::class)
    fun testSet() {
        excerciseEval("(set (quote alpha) 99)", "99")
        excerciseEval(
            "((lambda (x) (set (quote x) 99) (cons x x)) 23)",
            "(99 = 99)"
        )
    }

    // public void testSetViaPairEquals() throws Exception {
    // excerciseEval("(alpha = 777)", "777");
    // excerciseEval("(bravo = 888)", "888");
    // excerciseEval("(bravo = (+ 1 2))", "3");
    // excerciseEval("(bravo = (alpha = (- 222 100)))", "122");
    // excerciseEval("(bravo = ^(1 = 2))", "(1 = 2)");
    // }
    @kotlin.Throws(Exception::class)
    fun testCond() {
        excerciseEval("(cond)", "nil")
        excerciseEval("(cond (true 2))", "2")
        excerciseEval("(cond (nil 2))", "nil")
        excerciseEval("(cond (nil (cons 8 9)) (22 (cons 1 2)))", "(1 = 2)")
        excerciseEval("(cond (nil (cons 8 9)) (22 (cons 1 2) 44))", "44")
    }

    @kotlin.Throws(Exception::class)
    fun testCatch() {
        excerciseEval("(catch x (+ 9 1))", "10")
        excerciseEval("(catch x (++ 9 1))", "nil")
        excerciseEval("(catch x qweqweqwe)", "nil")
        excerciseEval(
            "(or (catch x qweqweqwe) x)",
            "'unbound variable: qweqweqwe'"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testRaise() {
        excerciseEval("(or (catch x (raise 1)) x)", "1")
        excerciseEval("(or (catch x (raise ^w)) x)", "w")
        excerciseEval("(or (catch x (raise ^(1))) x)", "(1)")
        excerciseEval("(or (catch x (raise 23.45)) x)", "23.45")
        excerciseEval(
            "(or (catch x (raise (dict (.a =3)))) x)",
            "(dict (.a = 3))"
        )
        excerciseEval("(or (catch x (raise 1)) x)", "1")
        excerciseEval("(or (catch x (raise ^w)) x)", "w")
        excerciseEval("(or (catch x (raise ^(1))) x)", "(1)")
        excerciseEval("(or (catch x (raise 23.45)) x)", "23.45")
        excerciseEval(
            "(or (catch x (raise (dict (.a =3)))) x)",
            "(dict (.a = 3))"
        )
    }
}
