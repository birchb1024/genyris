// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.exception.GenyrisException

class BuiltinInterpreterTests : TestCase() {
    private var interpreter: TestUtilities? = null

    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        interpreter = TestUtilities()
    }

    @kotlin.Throws(Exception::class)
    private fun exerciseEval(input: String?, expected: String?) {
        Assert.assertEquals(expected, interpreter!!.eval(input))
    }

    private fun exerciseBadEval(exp: String?) {
        try {
            interpreter!!.eval(exp)
            fail()
        } catch (e: GenyrisException) {
        }
    }

    @kotlin.Throws(Exception::class)
    fun testExerciseEval() {
        exerciseEval("(defvar (quote foo) 23)", "23")
        exerciseEval("foo", "23")
    }

    @kotlin.Throws(Exception::class)
    fun testExerciseEvalEnvironment() {
        exerciseEval("(1)", "1")
        exerciseEval("('a')", "'a'")
        exerciseEval("(^(1 2 3))", "(1 2 3)")
        exerciseEval("((dict (.s = 2)))", "(dict (.s = 2))")
        exerciseEval("((graph ^(s p o)))", "(graph (triple s p o))")
        exerciseEval("(^foo)", "foo")
        exerciseBadEval("(foo)")

        exerciseEval("(defvar (quote foo) 23)", "23")
        exerciseEval("foo", "23")
        exerciseEval("(foo)", "23")
    }

    @kotlin.Throws(Exception::class)
    fun testMath() {
        exerciseEval("(+ 2 3)", "5")
        exerciseEval("(- 2 3)", "-1")
        exerciseEval("(* 2 3)", "6")
        exerciseEval("(scale (/ 48 2) 0)", "24")
        exerciseEval("(% 3 2)", "1")
        exerciseEval("(< 2 3)", "true")
        exerciseEval("(> 2 3)", "nil")
        exerciseEval("(< 3 2)", "nil")
        exerciseEval("(> 3 2)", "true")
        exerciseEval("(power 2 10)", "1024")
        exerciseEval("(scale (sqrt 2) 10)", "1.4142135624")
    }

    @kotlin.Throws(Exception::class)
    fun testMathTrigonometry() {
        exerciseEval(
            "(@ns math 'http://www.genyris.org/lang/math#')math:pi",
            "3.1415926535897932384626433832795028841971"
        )
        exerciseEval("(@ns math 'http://www.genyris.org/lang/math#')(scale (sin math:pi) 6)", "0.000000")
        exerciseEval("(@ns math 'http://www.genyris.org/lang/math#')(scale (cos math:pi) 6)", "-1.000000")
        exerciseEval("(@ns math 'http://www.genyris.org/lang/math#')(scale (atan2 1 2) 9)", "0.463647609")
    }

    @kotlin.Throws(Exception::class)
    fun testNth() {
        exerciseEval("(nth 0 ^(a b c))", "a")
        exerciseEval("(nth 1 ^(a b c))", "b")
        exerciseEval("(nth 2 ^(a b c))", "c")
        exerciseBadEval("(nth 22 ^(a b c))")
        exerciseBadEval("(nth -1 ^(a b c))")
    }

    @kotlin.Throws(Exception::class)
    fun testSortNil() {
        exerciseEval("(sort nil)", "nil")
        exerciseEval("(sort ^())", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testSortSymbol() {
        exerciseEval("(sort ^(a b c))", "(a b c)")
        exerciseEval("(sort ^(z  x  y))", "(x y z)")
    }

    @kotlin.Throws(Exception::class)
    fun testSortString() {
        exerciseEval("(sort ^(\"z\"  \"x\"  \"y\"))", "(\"x\" \"y\" \"z\")")
    }

    @kotlin.Throws(Exception::class)
    fun testSortBignum() {
        exerciseEval("(sort ^(1))", "(1)")
        exerciseEval("(sort ^(123.123 -12 0.0 112.12 -200))", "(-200 -12 0.0 112.12 123.123)")
        exerciseEval("(sort ^(1755045567 1755045500 1755045599))", "(1755045500 1755045567 1755045599)")
    }

    @kotlin.Throws(Exception::class)
    fun testSortPair() {
        exerciseEval("(sort ^((1)))", "((1))")
        exerciseEval("(sort ^(()))", "(nil)")
        exerciseEval("(sort ^((123.123 -12)))", "((123.123 -12))")
        exerciseEval("(sort ^((5) (4) (3))))", "((3) (4) (5))")
        exerciseEval("(sort ^((5 9) (5 8) (3))))", "((3) (5 8) (5 9))")
        exerciseEval("(sort ^((5 = 9) (5 = 8) (3))))", "((3) (5 = 8) (5 = 9))")
        exerciseEval("(sort ^(((5 1) = (5 0)) (5 = 8) (3))))", "((3) (5 = 8) ((5 1) = (5 0)))")
        exerciseEval("(sort ^((d e f)(a b c)))", "((a b c) (d e f))")
        exerciseEval("(sort ^((a (222) c)(a (0) c)))", "((a (0) c) (a (222) c))")
        exerciseEval("(sort ^((9 (11 (111)) (9 (11 (777))))(a (0) c)))", "((a (0) c) (9 (11 (111)) (9 (11 (777)))))")
    }

    @kotlin.Throws(Exception::class)
    fun testSortPrefixSymbol() {
        exerciseEval("(@ns fu 'http://fu.fu/')", "EOF")
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(fu:zulu fu:argv fu:bannister))", "(fu:argv fu:bannister fu:zulu)")
        exerciseEval(
            "(@ns fu 'http://fu.fu/')(@ns ba 'http://ba.ba/')(sort ^(ba:zulu fu:bannister fu:argv))",
            "(ba:zulu fu:argv fu:bannister)"
        )
    }

    @kotlin.Throws(Exception::class)
    fun testSortEscapedSymbol() {
        exerciseEval("(sort ^(|zulu| |argv| |bannister|))", "(|argv| |bannister| |zulu|)")
        exerciseEval("(sort ^(|zulu| |argv| |http://fu.fu/alpha#|))", "(|argv| |http://fu.fu/alpha#| |zulu|)")
    }

    @kotlin.Throws(Exception::class)
    fun testSortDiversSymbol() {
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(zulu |argv| fu:bannister))", "(|argv| fu:bannister zulu)")
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(|argv| zulu  fu:bannister))", "(|argv| fu:bannister zulu)")
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(fu:bannister zulu |argv| ))", "(|argv| fu:bannister zulu)")
        exerciseEval("(left(sort (symlist)))", "%")
    }

    @kotlin.Throws(Exception::class)
    fun testSortBad() {
        exerciseBadEval("(sort 1)")
        exerciseBadEval("(sort 1 2)")
        exerciseBadEval("(sort ^(1 (2)))")
        exerciseBadEval("(sort ^(\"a\" 2))")
        exerciseBadEval("(sort (list (dict) (dict)))")
        exerciseBadEval("(sort (list ^A ^B ^  c 3 2 \"3\" \"e\" \"t\" (2) (3) (^w) (\"l\") (dict)))")
    }

    @kotlin.Throws(Exception::class)
    fun testSortTriples() {
        exerciseEval("(sort (list (triple ^q ^w 23)))", "((triple q w 23))")
        exerciseEval("(sort (list (triple ^q ^w 23) (triple ^a ^s 45)))", "((triple a s 45) (triple q w 23))")
        exerciseEval("(sort (list (triple ^q ^x 23) (triple ^q ^w 23)))", "((triple q w 23) (triple q x 23))")
        exerciseEval("(sort (list (triple ^q ^x 24) (triple ^q ^x 23)))", "((triple q x 23) (triple q x 24))")
    }

    @kotlin.Throws(Exception::class)
    fun testEquality() {
        exerciseEval("(equal? 1 1)", "true")
        exerciseEval("(equal? 1.2e4 1.2e4)", "true")
        exerciseEval("(equal? \"foo\" \"foo\")", "true")
        exerciseEval("(equal? ^sym ^sym)", "true")
    }

    @kotlin.Throws(Exception::class)
    fun testEqu() {
        exerciseEval("(defvar ^var 23)", "23")
        exerciseEval("(eq? 1 1)", "nil")
        exerciseEval("(eq? 1.2e4 1.2e4)", "nil")
        exerciseEval("(eq? \"foo\" \"foo\")", "nil")
        exerciseEval("(eq? ^sym ^sym)", "true")
        exerciseEval("(eq? var var)", "true")
    }

    @kotlin.Throws(Exception::class)
    fun testDict() {
        exerciseEval("(dict (.a = 1) (.b = 2))", "(dict (.a = 1) (.b = 2))")
        exerciseEval("(dict (.a) (.b = 2))", "(dict (.a = nil) (.b = 2))")
        exerciseEval("(dict (.a = ^(1)) (.b = 2))", "(dict (.a = (1)) (.b = 2))")
    }

    @kotlin.Throws(Exception::class)
    fun testAsString() {
        exerciseEval("(intern 'http://foo.bar/quux')", "|http://foo.bar/quux|")
        exerciseEval("(asString(intern 'http://foo.bar/quux'))", "'http://foo.bar/quux'")
        exerciseEval("(asString(intern 'https://foo.bar/quux'))", "'https://foo.bar/quux'")
        exerciseEval("(asString(intern ^|https://foo.bar/quux|))", "'https://foo.bar/quux'")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingWithNil() {
        exerciseEval("((graph)!asd)", "nil")
        exerciseEval("(nil!nil)", "nil")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingPair() {
        exerciseEval("(^(1 2)!^left)", "left")

        exerciseEval("(^(q w e)!0)", "q")
        exerciseEval("(^(q w e)!1)", "w")
        exerciseEval("(^(q w e)!(+ 1 1))", "e")
        exerciseEval("(^(q w e)!^1)", "w")

        exerciseEval("(^(1 2)!left)", "1")
        exerciseEval("(^(1 2)!.left)", "1")
        exerciseEval("(^(1 2)!right)", "(2)")
        exerciseEval("(^(1 2)!.right)", "(2)")

        exerciseEval("(defvar ^index 2)", "2")
        exerciseEval("(^(a s d)!(the index))", "d")
        exerciseEval("(^(a s d)!(index))", "d")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingPairSource() {
        exerciseEval("(^(1 2)!line-number)", "1")
        exerciseEval("(^(1 2)!.line-number)", "1")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingDictionary() {
        exerciseEval("(var d (dict (.age = 54) (.name = 'Jane')))", "(dict (.age = 54) (.name = 'Jane'))")
        exerciseEval("(d!age)", "54")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingBignum() {
        exerciseEval("(1!(+ .self .self))", "2")
        exerciseEval("(1;vars)", "(.self .vars .classes)")
        exerciseEval("(1!vars)", "(.self .vars .classes)")
        exerciseEval("(1!.vars)", "(.self .vars .classes)")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingString() {
        exerciseEval("('q';vars)", "(.self .vars .classes)")
        exerciseEval("('q'!vars)", "(.self .vars .classes)")
        exerciseEval("('q'!.vars)", "(.self .vars .classes)")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingExpression() {
        exerciseEval("(define X 1)", "1")
        exerciseEval("(define , eval)", "<EagerProc: eval>")

        exerciseEval("(1!(+ .self .self))", "2")
        exerciseEval("(^(i o p)!(eval X))", "o")
        exerciseEval("(^(i o p)!((lambda(x)x)X))", "o")
        exerciseEval("(^(i o p)!(, X))", "o")
        exerciseEval("(^(i o p)!(nil X))", "o")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingNest() {
        exerciseEval("(define L ^(1 2 3))", "(1 2 3)")
        exerciseEval("(L!1)", "2")
        exerciseEval("(L!(L!0))", "2")
        exerciseEval("(L!(L!(L!0)))", "3")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingMiscellany() {
        exerciseEval("(define W ^(a s d))", "(a s d)")
        exerciseEval("(define X 2)", "2")
        exerciseEval("((W!right)!0)", "s")
        exerciseEval("(W!(the X))", "d")
        exerciseEval("(W!(X))", "d")

        exerciseEval("(define G (graph ^(a s d) ^(S P O)))", "(graph (triple S P O) (triple a s d))")
        exerciseEval("(define S ^a)", "a")
        exerciseEval("(define dollar the)", "<EagerProc: the>")
        exerciseEval("(G!S)", "(graph (triple nil P O))")
        exerciseEval("(G!(S))", "(graph (triple nil s d))")
        exerciseEval("(G!(the S))", "(graph (triple nil s d))")
        exerciseEval("(G!\$S)", "(graph (triple nil s d))")
    }


    @kotlin.Throws(Exception::class)
    fun testSemiTriple() {
        exerciseEval("(var tr (triple ^s ^d 7))", "(triple s d 7)")

        exerciseEval("((triple ^s ^d 7);object)", "7")
        exerciseEval("(tr;predicate)", "d")
        exerciseEval("(tr;subject)", "s")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingTriple() {
        exerciseEval("((triple ^s ^d 7)!object)", "7")

        exerciseEval("(var tr (triple ^s ^d 7))", "(triple s d 7)")
        exerciseEval("(tr!predicate)", "d")
        exerciseEval("(tr!subject)", "s")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingGraph() {
        exerciseEval("(var g (graph))", "(graph)")
        exerciseEval("(g(.put ^sandnes ^locode 'ABCD'))", "(graph (triple sandnes locode 'ABCD'))")
        exerciseEval(
            "(g(.put ^sandnes ^coords  ^coord-s))",
            "(graph (triple sandnes coords coord-s) (triple sandnes locode 'ABCD'))"
        )
        exerciseEval(
            "(g(.put ^coord-s ^longitude 3.0))",
            "(graph (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD'))"
        )
        exerciseEval(
            "(g(.put ^coord-s ^latitude 3.0))",
            "(graph (triple coord-s latitude 3.0) (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD'))"
        )
        exerciseEval(
            "(g(.put ^sandnes ^portnum 123))",
            "(graph (triple coord-s latitude 3.0) (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD') (triple sandnes portnum 123))"
        )

        exerciseEval(
            "g",
            "(graph (triple coord-s latitude 3.0) (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD') (triple sandnes portnum 123))"
        )

        exerciseEval("(g(.subjects))", "(coord-s sandnes)")
        exerciseEval("(g(.predicates ^sandnes))", "(coords locode portnum)")
        exerciseEval("(g .length)", "5")
        exerciseEval("(g;length)", "5")

        exerciseEval("(g!sandnes!locode)", "('ABCD')")
        exerciseEval("(g!sandnes!locode!0)", "'ABCD'")
        exerciseEval("(g!not-a-subject)", "(graph)")
        exerciseEval(
            "(g!sandnes)",
            "(graph (triple nil coords coord-s) (triple nil locode 'ABCD') (triple nil portnum 123))"
        )

        exerciseEval("(g!sandnes!coords)", "(coord-s)")
        exerciseEval("(g!(g!sandnes!coords!0))", "(graph (triple nil latitude 3.0) (triple nil longitude 3.0))")
        exerciseEval(
            "(define coo (g!(g!sandnes!coords!0)))",
            "(graph (triple nil latitude 3.0) (triple nil longitude 3.0))"
        )
        exerciseEval("(coo!latitude!0)", "3.0")
        exerciseEval("((g!(g!sandnes!coords!0))!latitude!0)", "3.0")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingGraphList() {
        exerciseEval("(var h (graph))", "(graph)")
        exerciseEval(
            "(h(.put ^sandnes ^names ^('sandy' 'sndns' 'nes') ))",
            "(graph (triple sandnes names ('sandy' 'sndns' 'nes')))"
        )
        exerciseEval("(h!sandnes)", "(graph (triple nil names ('sandy' 'sndns' 'nes')))")
        exerciseEval("(h!sandnes!names)", "(('sandy' 'sndns' 'nes'))")
        exerciseEval("(h!sandnes!names!0!1)", "'sndns'")
    }

    @kotlin.Throws(Exception::class)
    fun testPlingGraphDictionary() {
        exerciseEval("(var h (graph))", "(graph)")
        exerciseEval(
            "(h(.put ^sandnes ^mayor (dict (.name = 'Jane')(.age = 54)) ))",
            "(graph (triple sandnes mayor (dict (.age = 54) (.name = 'Jane'))))"
        )
        exerciseEval("(h!sandnes)", "(graph (triple nil mayor (dict (.age = 54) (.name = 'Jane'))))")
        exerciseEval("(h!sandnes!mayor)", "((dict (.age = 54) (.name = 'Jane')))")
        exerciseEval("(h!sandnes!mayor!0)", "(dict (.age = 54) (.name = 'Jane'))")
        exerciseEval("(h!sandnes!mayor!0!name)", "'Jane'")
    }
}
