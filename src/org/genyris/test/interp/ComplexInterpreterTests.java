// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import junit.framework.TestCase;

import org.genyris.exception.GenyrisException;

public class ComplexInterpreterTests extends TestCase {

    private TestUtilities interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        interpreter = new TestUtilities();
    }

    private void exerciseEval(String exp, String expected) throws Exception {
        assertEquals(expected,  interpreter.eval(exp));
    }

    private void exerciseBadEval(String exp) {
        try {
			interpreter.eval(exp);
			fail();
		} catch (GenyrisException e) {}
    }

    public void testexerciseEval() throws Exception {
        exerciseEval("(defvar (quote foo) 23)", "23");
        exerciseEval("foo", "23");
    }

    public void testMacro() throws Exception {
        exerciseEval("(defvar ^w 99)", "99");
        exerciseEval("((lambdam (x) ^w) 45)", "99");
    }


    public void testMacroWithDefmacro() throws Exception {
        exerciseEval("(defmacro nil$ (x) (list ^defvar (list quote x) 0))", "<LazyProcedure: <nil$>>");
        exerciseEval("(nil$ a)", "0");
        exerciseEval("a", "0");
    }

    public void testDefs() throws Exception {
        exerciseEval("(def fn (a b c) 12)", "<EagerProc: <fn>>");
        exerciseEval("(df fn (x y z) 23)","<LazyProcedure: <anonymous lambdaq>>");
        exerciseEval("(defmacro fn (j k) (template ($j $k)))","<LazyProcedure: <fn>>");
    }

    public void testDefBad() throws Exception {
        exerciseBadEval("(def fn)");
        exerciseBadEval("(df fn)");
        exerciseBadEval("(defmacro fn)");
    }

    public void testMacroWithDefmacroDeep() throws Exception {
        exerciseEval("(def fn (y) (defmacro nil$ (x) (list ^defvar (list quote x) y)) nil$)", "<EagerProc: <fn>>");
        exerciseEval("(defvar ^m (fn 99))", "<LazyProcedure: <nil$>>");
        exerciseEval("(m w)", "99");
        exerciseEval("w", "99");
    }

    public void testMacroWithDefmacroDeep2() throws Exception {
        exerciseEval("(defvar ^y 7777)", "7777");
        exerciseEval("(def fn () (defmacro mac (x) (list ^defvar (list quote x) y)) mac)", "<EagerProc: <fn>>");
        exerciseEval("(def fun (y) (defvar ^m (fn)) m)", "<EagerProc: <fun>>");
        exerciseEval("((fun 5555) w)", "7777");
        exerciseEval("w", "7777");
    }

    public void testRecursion() throws Exception {
        exerciseEval("(defvar ^null (lambda (exp) (cond (exp nil) (true true))))", "<EagerProc: <anonymous lambda>>");
        exerciseEval("(defvar ^last (lambda (x) (cond ((null (cdr x)) (car x)) (true (last (cdr x))))))", "<EagerProc: <anonymous lambda>>");
        exerciseEval("(last ^(1 2 3 4))", "4");

    }

    public void testLexicalScope() throws Exception {
        exerciseEval("(defvar ^x -1)", "-1");
        exerciseEval("(defvar ^mk-func (lambda (x) (lambda (y) (cons x y))))", "<EagerProc: <anonymous lambda>>");
        exerciseEval("(mk-func 10)", "<EagerProc: <anonymous lambda>>");
        exerciseEval("((mk-func 10) 88)", "(10 = 88)");
    }

    public void testRestArgs() throws Exception {
        exerciseEval("(defvar ^fn (lambda (x &rest body) (list x body)))", "<EagerProc: <anonymous lambda>>");
        exerciseEval("(fn 1 2 3 4 5 6)", "(1 (2 3 4 5 6))");
        exerciseEval("(defvar ^fnq (lambdaq (x &rest body) body))", "<LazyProcedure: <anonymous lambdaq>>");
        exerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)");
        exerciseEval("(defvar ^fnq (lambdaq (x &rest body) body))", "<LazyProcedure: <anonymous lambdaq>>");
        exerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)");
        exerciseEval("(fnq foo bar 1 2)", "(bar 1 2)");

        exerciseEval("(defvar ^fnq (lambdam (x &rest body) body))", "<LazyProcedure: <anonymous lambdam>>");
        exerciseEval("(fnq 12 cons 1 2)", "(1 = 2)");
    }
    public void testFrame() throws Exception {
        exerciseEval("(dict (.a = 1) (.b = 2) (.c = 3))",
                "(dict (.a = 1) (.b = 2) (.c = 3))");
        exerciseEval("(eq? (dict (.a = 1) (.b = 2) (.c = 3)) (dict (.a = 1) (.b = 2) (.c = 3)))", "nil");
        exerciseEval("(equal? (dict (.a = 1) (.b = 2) (.c = 3)) (dict (.a = 1) (.b = 2) (.c = 3)))", "nil");
    }

    public void testEnvCapture() throws Exception {
        exerciseEval("(defvar ^mk-fn  (lambda (x) (defvar ^bal x) (defvar ^fn (lambda (y) (cons bal y))) fn))", "<EagerProc: <anonymous lambda>>");
        exerciseEval("(defvar ^ff (mk-fn 44))","<EagerProc: <anonymous lambda>>");
        exerciseEval("(ff 99)", "(44 = 99)");
    }
    public void testEnvCaptureWithDef() throws Exception {
        exerciseEval("(def mk-fn (x) (defvar ^bal x) (def fn (y) (cons bal y)) fn)", "<EagerProc: <mk-fn>>");
        exerciseEval("(defvar ^ff (mk-fn 44))","<EagerProc: <fn>>");
        exerciseEval("(ff 99)", "(44 = 99)");
    }


    public void testLeftRight() throws Exception {
        exerciseEval("(defvar ^p (cons 1 2))", "(1 = 2)");
        exerciseEval("(p .left)", "1");
        exerciseEval("(p .right)", "2");
        exerciseEval("(p (set ^.left 99))", "99");
        exerciseEval("p", "(99 = 2)");
        exerciseEval("(p (set ^.right 98))", "98");
        exerciseEval("p", "(99 = 98)");
    }

    public void testDynamicVariablesWithDef() throws Exception {
        exerciseEval("(defvar ^d (dict))", "(dict)");
        exerciseEval("(def function-which-declares-dynamic-var () (defvar ^.x 88) (function-which-uses-dynamic-var))","<EagerProc: <function-which-declares-dynamic-var>>");
        exerciseEval("(def function-which-uses-dynamic-var () (list .x .x))", "<EagerProc: <function-which-uses-dynamic-var>>");
        exerciseEval("(d (function-which-declares-dynamic-var))","(88 88)");
        exerciseEval("(bound? ^.x)","nil");
    }

    public void testDynamicVariablesWithDef2() throws Exception {
        exerciseEval("(defvar ^d (dict))", "(dict)");
        exerciseEval("(d (defvar ^.x 11111))", "11111");
        exerciseEval("(def define-some-global-y (x) (defvar ^.y 'global .y') (cons .x .y))", "<EagerProc: <define-some-global-y>>");
        exerciseEval("(d (define-some-global-y 33))", "(11111 = 'global .y')");
    }
    public void testMagicEnv() throws Exception {
        exerciseEval("(23 .self)", "23");
        exerciseEval("(23 (defvar ^x 43) x)", "43");
        exerciseEval("(23 (defvar ^x 43) (set ^x 99)x)", "99");
        exerciseEval("(23 (defvar ^.classes (list Bignum)) 3)", "3");
        exerciseBadEval("(23 (defvar ^.self 3)");
        exerciseBadEval("(23 (setq .left 3)");
        exerciseBadEval("(23 (setq .right 3)");
    }
	public void testParseAString() throws Exception {
		exerciseEval("((ParenParser(.new '(+ 1 2 3)'))(.read))", "(+ 1 2 3)");
	}
	public void testprefixeddynamic() throws Exception {
		exerciseEval("(@prefix erk 'http://foo/sys#')^.erk:foo",".|http://foo/sys#foo|");
	}
	
}
