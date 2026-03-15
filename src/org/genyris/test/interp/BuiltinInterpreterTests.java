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

    private void excerciseEval(String input, String expected) throws Exception {
        assertEquals(expected,  interpreter.eval(input));
    }

    private void excerciseBadEval(String exp) {
        try {
            interpreter.eval(exp);
            fail();
        } catch (GenyrisException e) {}
    }

    public void testExcerciseEval() throws Exception {
        excerciseEval("(defvar (quote foo) 23)", "23");
        excerciseEval("foo", "23");
    }

    public void testNth() throws Exception {
        excerciseEval("(nth 0 ^(a b c))", "a");
        excerciseEval("(nth 1 ^(a b c))", "b");
        excerciseEval("(nth 2 ^(a b c))", "c");
        excerciseBadEval("(nth 22 ^(a b c))");
        excerciseBadEval("(nth -1 ^(a b c))");
    }
    public void testSortNil() throws Exception {
        excerciseEval("(sort nil)", "nil");
        excerciseEval("(sort ^())", "nil");
    }
    public void testSortSymbol() throws Exception {
        excerciseEval("(sort ^(a b c))", "(a b c)");
        excerciseEval("(sort ^(z  x  y))", "(x y z)");
    }
    public void testSortString() throws Exception {
        excerciseEval("(sort ^(\"z\"  \"x\"  \"y\"))", "(\"x\" \"y\" \"z\")");
    }

    public void testSortBignum() throws Exception {
        excerciseEval("(sort ^(1))", "(1)");
        excerciseEval("(sort ^(123.123 -12 0.0 112.12 -200))", "(-200 -12 0.0 112.12 123.123)");
        excerciseEval("(sort ^(1755045567 1755045500 1755045599))", "(1755045500 1755045567 1755045599)");
    }

    public void testSortPair() throws Exception {
        excerciseEval("(sort ^((1)))", "((1))");
        excerciseEval("(sort ^(()))", "(nil)");
        excerciseEval("(sort ^((123.123 -12)))", "((123.123 -12))");
        excerciseEval("(sort ^((5) (4) (3))))", "((3) (4) (5))");
        excerciseEval("(sort ^((5 9) (5 8) (3))))", "((3) (5 8) (5 9))");
        excerciseEval("(sort ^((5 = 9) (5 = 8) (3))))", "((3) (5 = 8) (5 = 9))");
        excerciseEval("(sort ^(((5 1) = (5 0)) (5 = 8) (3))))", "((3) (5 = 8) ((5 1) = (5 0)))");
        excerciseEval("(sort ^((d e f)(a b c)))", "((a b c) (d e f))");
        excerciseEval("(sort ^((a (222) c)(a (0) c)))", "((a (0) c) (a (222) c))");
        excerciseEval("(sort ^((9 (11 (111)) (9 (11 (777))))(a (0) c)))", "((a (0) c) (9 (11 (111)) (9 (11 (777)))))");
    }
    public void testSortPrefixSymbol() throws Exception {
        excerciseEval("(@ns fu 'http://fu.fu/')", "EOF");
        excerciseEval("(@ns fu 'http://fu.fu/')(sort ^(fu:zulu fu:argv fu:bannister))", "(fu:argv fu:bannister fu:zulu)");
        excerciseEval("(@ns fu 'http://fu.fu/')(@ns ba 'http://ba.ba/')(sort ^(ba:zulu fu:bannister fu:argv))", "(ba:zulu fu:argv fu:bannister)");
    }
    public void testSortEscapedSymbol() throws Exception {
        excerciseEval("(sort ^(|zulu| |argv| |bannister|))", "(|argv| |bannister| |zulu|)");
        excerciseEval("(sort ^(|zulu| |argv| |http://fu.fu/alpha#|))", "(|argv| |http://fu.fu/alpha#| |zulu|)");
    }
    public void testSortDiversSymbol() throws Exception {
        excerciseEval("(@ns fu 'http://fu.fu/')(sort ^(zulu |argv| fu:bannister))", "(|argv| fu:bannister zulu)");
        excerciseEval("(@ns fu 'http://fu.fu/')(sort ^(|argv| zulu  fu:bannister))", "(|argv| fu:bannister zulu)");
        excerciseEval("(@ns fu 'http://fu.fu/')(sort ^(fu:bannister zulu |argv| ))", "(|argv| fu:bannister zulu)");
        excerciseEval("(left(sort (symlist)))", "%");
    }
    public void testSortBad() throws Exception {
        excerciseBadEval("(sort 1)");
        excerciseBadEval("(sort 1 2)");
        excerciseBadEval("(sort ^(1 (2)))");
        excerciseBadEval("(sort ^(\"a\" 2))");
        excerciseBadEval("(sort (list (dict) (dict)))");
        excerciseBadEval("(sort (list ^A ^B ^  c 3 2 \"3\" \"e\" \"t\" (2) (3) (^w) (\"l\") (dict)))");
    }
    public void testSortTriples() throws Exception {
        excerciseEval("(sort (list (triple ^q ^w 23)))", "((triple q w 23))");
        excerciseEval("(sort (list (triple ^q ^w 23) (triple ^a ^s 45)))", "((triple a s 45) (triple q w 23))");
        excerciseEval("(sort (list (triple ^q ^x 23) (triple ^q ^w 23)))", "((triple q w 23) (triple q x 23))");
        excerciseEval("(sort (list (triple ^q ^x 24) (triple ^q ^x 23)))", "((triple q x 23) (triple q x 24))");
    }

    public void testEquality() throws Exception {
        excerciseEval("(equal? 1 1)", "true");
        excerciseEval("(equal? 1.2e4 1.2e4)", "true");
        excerciseEval("(equal? \"foo\" \"foo\")", "true");
        excerciseEval("(equal? ^sym ^sym)", "true");
    }
    public void testEqu() throws Exception {
        excerciseEval("(defvar ^var 23)", "23");
        excerciseEval("(eq? 1 1)", "nil");
        excerciseEval("(eq? 1.2e4 1.2e4)", "nil");
        excerciseEval("(eq? \"foo\" \"foo\")", "nil");
        excerciseEval("(eq? ^sym ^sym)", "true");
        excerciseEval("(eq? var var)", "true");
    }
    public void testDict() throws Exception {
        excerciseEval("(dict (.a = 1) (.b = 2))","(dict (.a = 1) (.b = 2))");
        excerciseEval("(dict (.a) (.b = 2))", "(dict (.a = nil) (.b = 2))");
        excerciseEval("(dict (.a = ^(1)) (.b = 2))", "(dict (.a = (1)) (.b = 2))");
    }
    public void testAsString() throws Exception {
        excerciseEval("(intern 'http://foo.bar/quux')","|http://foo.bar/quux|");
        excerciseEval("(asString(intern 'http://foo.bar/quux'))","'http://foo.bar/quux'");
        excerciseEval("(asString(intern 'https://foo.bar/quux'))","'https://foo.bar/quux'");
        excerciseEval("(asString(intern ^|https://foo.bar/quux|))","'https://foo.bar/quux'");
    }

    public void testPlingPair() throws Exception {
        excerciseEval("(^(1 2)!^left)","left");

        excerciseEval("(^(q w e)!0)","q");
        excerciseEval("(^(q w e)!1)","w");
        excerciseEval("(^(q w e)!(+ 1 1))", "e");
        excerciseEval("(^(q w e)!^1)", "w");

        excerciseEval("(^(1 2)!left)","1");
        excerciseEval("(^(1 2)!right)","(2)");
        excerciseEval("(^(1 2)!.left)","1");
        excerciseEval("(^(1 2)!.right)","(2)");

        excerciseEval("(defvar ^index 2)","2");
        excerciseEval("(^(a s d)!index)","d");
    }

    public void testPlingBignum() throws Exception {
        excerciseEval("(1!(+ .self .self))","2");
        excerciseEval("(1;vars)","(.self .vars .classes)");
        excerciseEval("(1!vars)","(.self .vars .classes)");
        excerciseEval("(1!.vars)","(.self .vars .classes)");
    }

    public void testPlingString() throws Exception {
        excerciseEval("('q';vars)","(.self .vars .classes)");
        excerciseEval("('q'!vars)","(.self .vars .classes)");
        excerciseEval("('q'!.vars)","(.self .vars .classes)");
    }
}
