// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.exception.GenyrisException
import org.genyris.interp.Interpreter
import org.genyris.io.InStream
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import org.genyris.io.parser.ParserXML

class ComplexInterpreterTests : TestCase() {
    private var interpreter: TestUtilities? = null

    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        interpreter = TestUtilities()
    }

    @kotlin.Throws(Exception::class)
    private fun exerciseEval(exp: String?, expected: String?) {
        Assert.assertEquals(expected, interpreter!!.eval(exp))
    }

    private fun exerciseBadEval(exp: String?) {
        try {
            interpreter!!.eval(exp)
            fail()
        } catch (e: GenyrisException) {
        }
    }

    @kotlin.Throws(Exception::class)
    fun testexerciseEval() {
        exerciseEval("(defvar (quote foo) 23)", "23")
        exerciseEval("foo", "23")
    }

    @kotlin.Throws(Exception::class)
    fun testMacro() {
        exerciseEval("(defvar ^w 99)", "99")
        exerciseEval("((lambdam (x) ^w) 45)", "99")
    }


    @kotlin.Throws(Exception::class)
    fun testMacroWithDefmacro() {
        exerciseEval("(defmacro nil$ (x) (list ^defvar (list quote x) 0))", "<LazyProcedure: <nil$>>")
        exerciseEval("(nil$ a)", "0")
        exerciseEval("a", "0")
    }

    @kotlin.Throws(Exception::class)
    fun testDefs() {
        exerciseEval("(def fn (a b c) 12)", "<EagerProc: <fn>>")
        exerciseEval("(df fn (x y z) 23)", "<LazyProcedure: <anonymous lambdaq>>")
        exerciseEval("(defmacro fn (j k) (template (\$j \$k)))", "<LazyProcedure: <fn>>")
    }

    @kotlin.Throws(Exception::class)
    fun testDefBad() {
        exerciseBadEval("(def fn)")
        exerciseBadEval("(df fn)")
        exerciseBadEval("(defmacro fn)")
    }

    @kotlin.Throws(Exception::class)
    fun testMacroWithDefmacroDeep() {
        exerciseEval("(def fn (y) (defmacro nil$ (x) (list ^defvar (list quote x) y)) nil$)", "<EagerProc: <fn>>")
        exerciseEval("(defvar ^m (fn 99))", "<LazyProcedure: <nil$>>")
        exerciseEval("(m w)", "99")
        exerciseEval("w", "99")
    }

    @kotlin.Throws(Exception::class)
    fun testMacroWithDefmacroDeep2() {
        exerciseEval("(defvar ^y 7777)", "7777")
        exerciseEval("(def fn () (defmacro mac (x) (list ^defvar (list quote x) y)) mac)", "<EagerProc: <fn>>")
        exerciseEval("(def fun (y) (defvar ^m (fn)) m)", "<EagerProc: <fun>>")
        exerciseEval("((fun 5555) w)", "7777")
        exerciseEval("w", "7777")
    }

    @kotlin.Throws(Exception::class)
    fun testRecursion() {
        exerciseEval("(defvar ^null (lambda (exp) (cond (exp nil) (true true))))", "<EagerProc: <anonymous lambda>>")
        exerciseEval(
            "(defvar ^last (lambda (x) (cond ((null (cdr x)) (car x)) (true (last (cdr x))))))",
            "<EagerProc: <anonymous lambda>>"
        )
        exerciseEval("(last ^(1 2 3 4))", "4")
    }

    @kotlin.Throws(Exception::class)
    fun testLexicalScope() {
        exerciseEval("(defvar ^x -1)", "-1")
        exerciseEval("(defvar ^mk-func (lambda (x) (lambda (y) (cons x y))))", "<EagerProc: <anonymous lambda>>")
        exerciseEval("(mk-func 10)", "<EagerProc: <anonymous lambda>>")
        exerciseEval("((mk-func 10) 88)", "(10 = 88)")
    }

    @kotlin.Throws(Exception::class)
    fun testRestArgs() {
        exerciseEval("(defvar ^fn (lambda (x &rest body) (list x body)))", "<EagerProc: <anonymous lambda>>")
        exerciseEval("(fn 1 2 3 4 5 6)", "(1 (2 3 4 5 6))")
        exerciseEval("(defvar ^fnq (lambdaq (x &rest body) body))", "<LazyProcedure: <anonymous lambdaq>>")
        exerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)")
        exerciseEval("(defvar ^fnq (lambdaq (x &rest body) body))", "<LazyProcedure: <anonymous lambdaq>>")
        exerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)")
        exerciseEval("(fnq foo bar 1 2)", "(bar 1 2)")

        exerciseEval("(defvar ^fnq (lambdam (x &rest body) body))", "<LazyProcedure: <anonymous lambdam>>")
        exerciseEval("(fnq 12 cons 1 2)", "(1 = 2)")
    }

    @kotlin.Throws(Exception::class)
    fun testFrame() {
        exerciseEval(
            "(dict (.a = 1) (.b = 2) (.c = 3))",
            "(dict (.a = 1) (.b = 2) (.c = 3))"
        )
        exerciseEval("(eq? (dict (.a = 1) (.b = 2) (.c = 3)) (dict (.a = 1) (.b = 2) (.c = 3)))", "nil")
        exerciseEval("(equal? (dict (.a = 1) (.b = 2) (.c = 3)) (dict (.a = 1) (.b = 2) (.c = 3)))", "true")
    }

    @kotlin.Throws(Exception::class)
    fun testEnvCapture() {
        exerciseEval(
            "(defvar ^mk-fn  (lambda (x) (defvar ^bal x) (defvar ^fn (lambda (y) (cons bal y))) fn))",
            "<EagerProc: <anonymous lambda>>"
        )
        exerciseEval("(defvar ^ff (mk-fn 44))", "<EagerProc: <anonymous lambda>>")
        exerciseEval("(ff 99)", "(44 = 99)")
    }

    @kotlin.Throws(Exception::class)
    fun testEnvCaptureWithDef() {
        exerciseEval("(def mk-fn (x) (defvar ^bal x) (def fn (y) (cons bal y)) fn)", "<EagerProc: <mk-fn>>")
        exerciseEval("(defvar ^ff (mk-fn 44))", "<EagerProc: <fn>>")
        exerciseEval("(ff 99)", "(44 = 99)")
    }


    @kotlin.Throws(Exception::class)
    fun testLeftRight() {
        exerciseEval("(defvar ^p (cons 1 2))", "(1 = 2)")
        exerciseEval("(p .left)", "1")
        exerciseEval("(p .right)", "2")
        exerciseEval("(p (set ^.left 99))", "99")
        exerciseEval("p", "(99 = 2)")
        exerciseEval("(p (set ^.right 98))", "98")
        exerciseEval("p", "(99 = 98)")
    }

    @kotlin.Throws(Exception::class)
    fun testDynamicVariablesWithDef() {
        exerciseEval("(defvar ^d (dict))", "(dict)")
        exerciseEval(
            "(def function-which-declares-dynamic-var () (defvar ^.x 88) (function-which-uses-dynamic-var))",
            "<EagerProc: <function-which-declares-dynamic-var>>"
        )
        exerciseEval(
            "(def function-which-uses-dynamic-var () (list .x .x))",
            "<EagerProc: <function-which-uses-dynamic-var>>"
        )
        exerciseEval("(d (function-which-declares-dynamic-var))", "(88 88)")
        exerciseEval("(bound? ^.x)", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testDynamicVariablesWithDef2() {
        exerciseEval("(defvar ^d (dict))", "(dict)")
        exerciseEval("(d (defvar ^.x 11111))", "11111")
        exerciseEval(
            "(def define-some-global-y (x) (defvar ^.y 'global .y') (cons .x .y))",
            "<EagerProc: <define-some-global-y>>"
        )
        exerciseEval("(d (define-some-global-y 33))", "(11111 = 'global .y')")
    }

    @kotlin.Throws(Exception::class)
    fun testMagicEnv() {
        exerciseEval("(23 .self)", "23")
        exerciseEval("(23 (defvar ^x 43) x)", "43")
        exerciseEval("(23 (defvar ^x 43) (set ^x 99)x)", "99")
        exerciseEval("(23 (defvar ^.classes (list Bignum)) 3)", "3")
        exerciseBadEval("(23 (defvar ^.self 3)")
        exerciseBadEval("(23 (setq .left 3)")
        exerciseBadEval("(23 (setq .right 3)")
    }

    @kotlin.Throws(Exception::class)
    fun testParseAString() {
        exerciseEval("((ParenParser(.new '(+ 1 2 3)'))(.read))", "(+ 1 2 3)")
    }

    @kotlin.Throws(Exception::class)
    fun testprefixeddynamic() {
        exerciseEval("(@ns erk 'http://foo/sys#')^.erk:foo", ".erk:foo")
    }

    @kotlin.Throws(Exception::class)
    fun testParseXMLString() {
        val input: String = """
                        <?xml version="1.0" encoding="utf-8"?>
                        <rdf:Description xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/terms/">
                               	<dc:title xml:lang="en">Doors Next</dc:title>
                        </rdf:Description>
                        """.trimIndent()
        val big =
            "((('dc' = 'http://purl.org/dc/terms/') ('rdf' = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#')) (|http://www.w3.org/1999/02/22-rdf-syntax-ns#Description| nil ((|http://purl.org/dc/terms/title| ((|http://www.w3.org/XML/1998/namespacelang| = 'en')) 'Doors Next'))))"
        evalForXML(input, big, true)

        val little =
            "((('dc' = 'http://purl.org/dc/terms/') ('rdf' = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#')) (rdf:Description nil ((dc:title ((xml:lang = 'en')) 'Doors Next'))))"
        evalForXML(input, little, false)
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        private fun evalForXML(input: String, expected: String?, expandAbbreviation: Boolean) {
            val fd: InStream = UngettableInStream(StringInStream(input))
            val interp = Interpreter()
            interp.init(false, "evalForXML")
            val parser = ParserXML(interp.getSymbolTable(), fd)
            val result = parser.read(interp.getGlobalEnv())
            Assert.assertEquals(expected, (TestUtilities()).renderExp(result, expandAbbreviation))
        }
    }
}
