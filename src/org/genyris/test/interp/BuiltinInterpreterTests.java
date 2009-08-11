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

    private void excerciseEval(String exp, String expected) throws Exception {
        assertEquals(expected,  interpreter.eval(exp));
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
        excerciseEval("(nth 0 '(a b c))", "a");
        excerciseEval("(nth 1 '(a b c))", "b");
        excerciseEval("(nth 2 '(a b c))", "c");
        excerciseBadEval("(nth 22 '(a b c))");
        excerciseBadEval("(nth -1 '(a b c))");
    }
    public void testEquality() throws Exception {
        excerciseEval("(equal? 1 1)", "true");
        excerciseEval("(equal? 1.2e4 1.2e4)", "true");
        excerciseEval("(equal? \"foo\" \"foo\")", "true");
        excerciseEval("(equal? 'sym 'sym)", "true");
    }
    public void testEqu() throws Exception {
        excerciseEval("(defvar 'var 23)", "23");
        excerciseEval("(eq? 1 1)", "nil");
        excerciseEval("(eq? 1.2e4 1.2e4)", "nil");
        excerciseEval("(eq? \"foo\" \"foo\")", "nil");
        excerciseEval("(eq? 'sym 'sym)", "true");
        excerciseEval("(eq? var var)", "true");
    }
    public void testDict() throws Exception {
        excerciseEval("(dict (!a : 1) (!b : 2))","(dict (a : 1) (b : 2))");
        excerciseEval("(dict (!a) (!b : 2))", "(dict (a : nil) (b : 2))");
        excerciseEval("(dict (!a : '(1)) (!b : 2))", "(dict (a : (1)) (b : 2))");
    }

}
