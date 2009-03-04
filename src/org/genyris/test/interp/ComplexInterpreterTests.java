// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import junit.framework.TestCase;

public class ComplexInterpreterTests extends TestCase {

    private TestUtilities interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        interpreter = new TestUtilities();
    }

    private void excerciseEval(String exp, String expected) throws Exception {
        assertEquals(expected,  interpreter.eval(exp));
    }

    public void testExcerciseEval() throws Exception {
        excerciseEval("(defvar (quote foo) 23)", "23");
        excerciseEval("foo", "23");
    }

    public void testMacro() throws Exception {
        excerciseEval("(defvar 'w 99)", "99");
        excerciseEval("((lambdam (x) 'w) 45)", "99");
    }


    public void testMacroWithDefmacro() throws Exception {
        excerciseEval("(defmacro nil^ (x) (list 'defvar (list quote x) 0))", "<LazyProcedure: <nil^>>");
        excerciseEval("(nil^ a)", "0");
        excerciseEval("a", "0");
    }

    public void testMacroWithDefmacroDeep() throws Exception {
        excerciseEval("(def fn (y) (defmacro nil^ (x) (list 'defvar (list quote x) y)) nil^)", "<EagerProc: <fn>>");
        excerciseEval("(defvar 'm (fn 99))", "<LazyProcedure: <nil^>>");
        excerciseEval("(m w)", "99");
        excerciseEval("w", "99");
    }

    public void testMacroWithDefmacroDeep2() throws Exception {
        excerciseEval("(defvar 'y 7777)", "7777");
        excerciseEval("(def fn () (defmacro mac (x) (list 'defvar (list quote x) y)) mac)", "<EagerProc: <fn>>");
        excerciseEval("(def fun (y) (defvar 'm (fn)) m)", "<EagerProc: <fun>>");
        excerciseEval("((fun 5555) w)", "7777");
        excerciseEval("w", "7777");
    }

    public void testRecursion() throws Exception {
        excerciseEval("(defvar 'null (lambda (exp) (cond (exp nil) (true true))))", "<EagerProc: <anonymous lambda>>");
        excerciseEval("(defvar 'last (lambda (x) (cond ((null (cdr x)) (car x)) (true (last (cdr x))))))", "<EagerProc: <anonymous lambda>>");
        excerciseEval("(last '(1 2 3 4))", "4");

    }

    public void testLexicalScope() throws Exception {
        excerciseEval("(defvar 'x -1)", "-1");
        excerciseEval("(defvar 'mk-func (lambda (x) (lambda (y) (cons x y))))", "<EagerProc: <anonymous lambda>>");
        excerciseEval("(mk-func 10)", "<EagerProc: <anonymous lambda>>");
        excerciseEval("((mk-func 10) 88)", "(10 : 88)");
    }

    public void testRestArgs() throws Exception {
        excerciseEval("(defvar 'fn (lambda (x &rest body) (list x body)))", "<EagerProc: <anonymous lambda>>");
        excerciseEval("(fn 1 2 3 4 5 6)", "(1 (2 3 4 5 6))");
        excerciseEval("(defvar 'fnq (lambdaq (x &rest body) body))", "<LazyProcedure: <anonymous lambdaq>>");
        excerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)");
        excerciseEval("(defvar 'fnq (lambdaq (x &rest body) body))", "<LazyProcedure: <anonymous lambdaq>>");
        excerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)");
        excerciseEval("(fnq foo bar 1 2)", "(bar 1 2)");

        excerciseEval("(defvar 'fnq (lambdam (x &rest body) body))", "<LazyProcedure: <anonymous lambdam>>");
        excerciseEval("(fnq 12 cons 1 2)", "(1 : 2)");
    }
    public void testFrame() throws Exception {
        excerciseEval("(dict (!a : 1) (!b:2) (!c:3))",
                "(dict (a : 1) (b : 2) (c : 3))");
        excerciseEval("(eq? (dict (!a : 1) (!b : 2) (!c : 3)) (dict (!a : 1) (!b : 2) (!c : 3)))", "nil");
        excerciseEval("(equal? (dict (!a : 1) (!b : 2) (!c : 3)) (dict (!a : 1) (!b : 2) (!c : 3)))", "true");
    }

    public void testEnvCapture() throws Exception {
        excerciseEval("(defvar 'mk-fn  (lambda (x) (defvar 'bal x) (defvar 'fn (lambda (y) (cons bal y))) fn))", "<EagerProc: <anonymous lambda>>");
        excerciseEval("(defvar 'ff (mk-fn 44))","<EagerProc: <anonymous lambda>>");
        excerciseEval("(ff 99)", "(44 : 99)");
    }
    public void testEnvCaptureWithDef() throws Exception {
        excerciseEval("(def mk-fn (x) (defvar 'bal x) (def fn (y) (cons bal y)) fn)", "<EagerProc: <mk-fn>>");
        excerciseEval("(defvar 'ff (mk-fn 44))","<EagerProc: <fn>>");
        excerciseEval("(ff 99)", "(44 : 99)");
    }


    public void testLeftRight() throws Exception {
        excerciseEval("(defvar 'p (cons 1 2))", "(1 : 2)");
        excerciseEval("(p !left)", "1");
        excerciseEval("(p !right)", "2");
        excerciseEval("(p (set '!left 99))", "99");
        excerciseEval("p", "(99 : 2)");
        excerciseEval("(p (set '!right 98))", "98");
        excerciseEval("p", "(99 : 98)");
    }

    public void testDynamicVariablesWithDef() throws Exception {
        excerciseEval("(defvar 'd (dict))", "(dict)");
        excerciseEval("(def function-which-declares-dynamic-var () (defvar '!x 88) (function-which-uses-dynamic-var))","<EagerProc: <function-which-declares-dynamic-var>>");
        excerciseEval("(def function-which-uses-dynamic-var () (list !x !x))", "<EagerProc: <function-which-uses-dynamic-var>>");
        excerciseEval("(d (function-which-declares-dynamic-var))","(88 88)");
        excerciseEval("(bound? !x)","nil");
    }

    public void testDynamicVariablesWithDef2() throws Exception {
        excerciseEval("(defvar 'd (dict))", "(dict)");
        excerciseEval("(d (defvar '!x 11111))", "11111");
        excerciseEval("(def define-some-global-y (x) (defvar '!y \"global !y\") (cons !x !y))", "<EagerProc: <define-some-global-y>>");
        excerciseEval("(d (define-some-global-y 33))", "(11111 : \"global !y\")");
    }
}
