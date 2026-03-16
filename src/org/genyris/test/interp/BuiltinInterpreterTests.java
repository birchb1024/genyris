// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import junit.framework.TestCase;

import org.genyris.exception.GenyrisException;

public class BuiltinInterpreterTests extends TestCase {

    private TestUtilities interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        interpreter = new TestUtilities();
    }

    private void exerciseEval(String input, String expected) throws Exception {
        assertEquals(expected,  interpreter.eval(input));
    }

    private void exerciseBadEval(String exp) {
        try {
            interpreter.eval(exp);
            fail();
        } catch (GenyrisException e) {}
    }

    public void testExerciseEval() throws Exception {
        exerciseEval("(defvar (quote foo) 23)", "23");
        exerciseEval("foo", "23");
    }

    public void testNth() throws Exception {
        exerciseEval("(nth 0 ^(a b c))", "a");
        exerciseEval("(nth 1 ^(a b c))", "b");
        exerciseEval("(nth 2 ^(a b c))", "c");
        exerciseBadEval("(nth 22 ^(a b c))");
        exerciseBadEval("(nth -1 ^(a b c))");
    }
    public void testSortNil() throws Exception {
        exerciseEval("(sort nil)", "nil");
        exerciseEval("(sort ^())", "nil");
    }
    public void testSortSymbol() throws Exception {
        exerciseEval("(sort ^(a b c))", "(a b c)");
        exerciseEval("(sort ^(z  x  y))", "(x y z)");
    }
    public void testSortString() throws Exception {
        exerciseEval("(sort ^(\"z\"  \"x\"  \"y\"))", "(\"x\" \"y\" \"z\")");
    }

    public void testSortBignum() throws Exception {
        exerciseEval("(sort ^(1))", "(1)");
        exerciseEval("(sort ^(123.123 -12 0.0 112.12 -200))", "(-200 -12 0.0 112.12 123.123)");
        exerciseEval("(sort ^(1755045567 1755045500 1755045599))", "(1755045500 1755045567 1755045599)");
    }

    public void testSortPair() throws Exception {
        exerciseEval("(sort ^((1)))", "((1))");
        exerciseEval("(sort ^(()))", "(nil)");
        exerciseEval("(sort ^((123.123 -12)))", "((123.123 -12))");
        exerciseEval("(sort ^((5) (4) (3))))", "((3) (4) (5))");
        exerciseEval("(sort ^((5 9) (5 8) (3))))", "((3) (5 8) (5 9))");
        exerciseEval("(sort ^((5 = 9) (5 = 8) (3))))", "((3) (5 = 8) (5 = 9))");
        exerciseEval("(sort ^(((5 1) = (5 0)) (5 = 8) (3))))", "((3) (5 = 8) ((5 1) = (5 0)))");
        exerciseEval("(sort ^((d e f)(a b c)))", "((a b c) (d e f))");
        exerciseEval("(sort ^((a (222) c)(a (0) c)))", "((a (0) c) (a (222) c))");
        exerciseEval("(sort ^((9 (11 (111)) (9 (11 (777))))(a (0) c)))", "((a (0) c) (9 (11 (111)) (9 (11 (777)))))");
    }
    public void testSortPrefixSymbol() throws Exception {
        exerciseEval("(@ns fu 'http://fu.fu/')", "EOF");
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(fu:zulu fu:argv fu:bannister))", "(fu:argv fu:bannister fu:zulu)");
        exerciseEval("(@ns fu 'http://fu.fu/')(@ns ba 'http://ba.ba/')(sort ^(ba:zulu fu:bannister fu:argv))", "(ba:zulu fu:argv fu:bannister)");
    }
    public void testSortEscapedSymbol() throws Exception {
        exerciseEval("(sort ^(|zulu| |argv| |bannister|))", "(|argv| |bannister| |zulu|)");
        exerciseEval("(sort ^(|zulu| |argv| |http://fu.fu/alpha#|))", "(|argv| |http://fu.fu/alpha#| |zulu|)");
    }
    public void testSortDiversSymbol() throws Exception {
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(zulu |argv| fu:bannister))", "(|argv| fu:bannister zulu)");
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(|argv| zulu  fu:bannister))", "(|argv| fu:bannister zulu)");
        exerciseEval("(@ns fu 'http://fu.fu/')(sort ^(fu:bannister zulu |argv| ))", "(|argv| fu:bannister zulu)");
        exerciseEval("(left(sort (symlist)))", "%");
    }
    public void testSortBad() throws Exception {
        exerciseBadEval("(sort 1)");
        exerciseBadEval("(sort 1 2)");
        exerciseBadEval("(sort ^(1 (2)))");
        exerciseBadEval("(sort ^(\"a\" 2))");
        exerciseBadEval("(sort (list (dict) (dict)))");
        exerciseBadEval("(sort (list ^A ^B ^  c 3 2 \"3\" \"e\" \"t\" (2) (3) (^w) (\"l\") (dict)))");
    }
    public void testSortTriples() throws Exception {
        exerciseEval("(sort (list (triple ^q ^w 23)))", "((triple q w 23))");
        exerciseEval("(sort (list (triple ^q ^w 23) (triple ^a ^s 45)))", "((triple a s 45) (triple q w 23))");
        exerciseEval("(sort (list (triple ^q ^x 23) (triple ^q ^w 23)))", "((triple q w 23) (triple q x 23))");
        exerciseEval("(sort (list (triple ^q ^x 24) (triple ^q ^x 23)))", "((triple q x 23) (triple q x 24))");
    }

    public void testEquality() throws Exception {
        exerciseEval("(equal? 1 1)", "true");
        exerciseEval("(equal? 1.2e4 1.2e4)", "true");
        exerciseEval("(equal? \"foo\" \"foo\")", "true");
        exerciseEval("(equal? ^sym ^sym)", "true");
    }
    public void testEqu() throws Exception {
        exerciseEval("(defvar ^var 23)", "23");
        exerciseEval("(eq? 1 1)", "nil");
        exerciseEval("(eq? 1.2e4 1.2e4)", "nil");
        exerciseEval("(eq? \"foo\" \"foo\")", "nil");
        exerciseEval("(eq? ^sym ^sym)", "true");
        exerciseEval("(eq? var var)", "true");
    }
    public void testDict() throws Exception {
        exerciseEval("(dict (.a = 1) (.b = 2))","(dict (.a = 1) (.b = 2))");
        exerciseEval("(dict (.a) (.b = 2))", "(dict (.a = nil) (.b = 2))");
        exerciseEval("(dict (.a = ^(1)) (.b = 2))", "(dict (.a = (1)) (.b = 2))");
    }
    public void testAsString() throws Exception {
        exerciseEval("(intern 'http://foo.bar/quux')","|http://foo.bar/quux|");
        exerciseEval("(asString(intern 'http://foo.bar/quux'))","'http://foo.bar/quux'");
        exerciseEval("(asString(intern 'https://foo.bar/quux'))","'https://foo.bar/quux'");
        exerciseEval("(asString(intern ^|https://foo.bar/quux|))","'https://foo.bar/quux'");
    }

    public void testPlingPair() throws Exception {
        exerciseEval("(^(1 2)!^left)","left");

        exerciseEval("(^(q w e)!0)","q");
        exerciseEval("(^(q w e)!1)","w");
        exerciseEval("(^(q w e)!(+ 1 1))", "e");
        exerciseEval("(^(q w e)!^1)", "w");

        exerciseEval("(^(1 2)!left)","1");
        exerciseEval("(^(1 2)!right)","(2)");
        exerciseEval("(^(1 2)!.left)","2"); // because .left returns 1 which is reevaled
        exerciseEval("(^(1 2)!.right)","(2)");

        exerciseEval("(defvar ^index 2)","2");
        exerciseEval("(^(a s d)!index)","d");
    }

    public void testPlingDictionary() throws Exception {
        exerciseEval("(var d (dict (.age = 54) (.name = 'Jane')))","(dict (.age = 54) (.name = 'Jane'))");
        exerciseEval("(d!age)","54");
    }

    public void testPlingBignum() throws Exception {
        exerciseEval("(1!(+ .self .self))","2");
        exerciseEval("(1;vars)","(.self .vars .classes)");
        exerciseEval("(1!vars)","(.self .vars .classes)");
        exerciseEval("(1!.vars)","(.self .vars .classes)");
    }

    public void testPlingString() throws Exception {
        exerciseEval("('q';vars)","(.self .vars .classes)");
        exerciseEval("('q'!vars)","(.self .vars .classes)");
        exerciseEval("('q'!.vars)","(.self .vars .classes)");
    }

    public void testPlingExpression() throws Exception {
        exerciseEval("(define X 1)","1");
        exerciseEval("(define , eval)","<EagerProc: eval>");

        exerciseEval("(1!(+ .self .self))","2");
        exerciseEval("(^(i o p)!(eval X))","o");
        exerciseEval("(^(i o p)!((lambda(x)x)X))","o");
        exerciseEval("(^(i o p)!(, X))","o");
        exerciseEval("(^(i o p)!(nil X))","o");
    }

    public void testPlingNest() throws Exception {
        exerciseEval("(define L ^(1 2 3))","(1 2 3)");
        exerciseEval("(L!1)","2");
        exerciseEval("(L!(L!0))","2");
        exerciseEval("(L!(L!(L!0)))","3");

        exerciseEval("(define W ^(a s d))","(a s d)");
        exerciseEval("(define X 2)","2");
        exerciseEval("((W!right)!0)","s");
        exerciseEval("(W!X)","d");
    }

    public void testPlingGraph() throws Exception {
        exerciseEval("(var g (graph))","(graph)");
        exerciseEval("(g(.put ^sandnes ^locode 'ABCD'))","(graph (triple sandnes locode 'ABCD'))");
        exerciseEval("(g(.put ^sandnes ^coords  ^coord-s))","(graph (triple sandnes coords coord-s) (triple sandnes locode 'ABCD'))");
        exerciseEval("(g(.put ^coord-s ^longitude 3.0))","(graph (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD'))");
        exerciseEval("(g(.put ^coord-s ^latitude 3.0))","(graph (triple coord-s latitude 3.0) (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD'))");
        exerciseEval("(g(.put ^sandnes ^portnum 123))","(graph (triple coord-s latitude 3.0) (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD') (triple sandnes portnum 123))");

        exerciseEval("g","(graph (triple coord-s latitude 3.0) (triple coord-s longitude 3.0) (triple sandnes coords coord-s) (triple sandnes locode 'ABCD') (triple sandnes portnum 123))");

        exerciseEval("(g(.subjects))","(coord-s sandnes)");
        exerciseEval("(g(.predicates ^sandnes))","(coords locode portnum)");
        exerciseEval("(g .length)","5");
        exerciseEval("(g;length)","5");

        exerciseEval("(g!sandnes!locode)","('ABCD')");
        exerciseEval("(g!sandnes!locode!0)","'ABCD'");
        exerciseEval("(g!not-a-subject)","(graph)");
        exerciseEval("(g!sandnes)","(graph (triple nil coords coord-s) (triple nil locode 'ABCD') (triple nil portnum 123))");

        exerciseEval("(g!sandnes!coords)","(coord-s)");
        exerciseEval("(g!(g!sandnes!coords!0))","(graph (triple nil latitude 3.0) (triple nil longitude 3.0))");
        exerciseEval("(define coo (g!(g!sandnes!coords!0)))","(graph (triple nil latitude 3.0) (triple nil longitude 3.0))");
        exerciseEval("(coo!latitude!0)","3.0");
        exerciseEval("((g!(g!sandnes!coords!0))!latitude!0)","3.0");

    }
    public void testPlingGraphList() throws Exception {
        exerciseEval("(var h (graph))","(graph)");
        exerciseEval("(h(.put ^sandnes ^names ^('sandy' 'sndns' 'nes') ))","(graph (triple sandnes names ('sandy' 'sndns' 'nes')))");
        exerciseEval("(h!sandnes)","(graph (triple nil names ('sandy' 'sndns' 'nes')))");
        exerciseEval("(h!sandnes!names)","(('sandy' 'sndns' 'nes'))");
        exerciseEval("(h!sandnes!names!0!1)","'sndns'");
    }

    public void testPlingGraphDictionary() throws Exception {
        exerciseEval("(var h (graph))","(graph)");
        exerciseEval("(h(.put ^sandnes ^mayor (dict (.name = 'Jane')(.age = 54)) ))","(graph (triple sandnes mayor (dict (.age = 54) (.name = 'Jane'))))");
        exerciseEval("(h!sandnes)","(graph (triple nil mayor (dict (.age = 54) (.name = 'Jane'))))");
        exerciseEval("(h!sandnes!mayor)","((dict (.age = 54) (.name = 'Jane')))");
        exerciseEval("(h!sandnes!mayor!0)","(dict (.age = 54) (.name = 'Jane'))");
        exerciseEval("(h!sandnes!mayor!0!name)","'Jane'");
    }
}
