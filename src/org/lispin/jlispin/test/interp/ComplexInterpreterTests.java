package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

public class ComplexInterpreterTests extends TestCase {
	
	private TestUtilities interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new TestUtilities();
	}
	
	private void excerciseEval(String exp, String expected) throws Exception {
		assertEquals(expected, interpreter.eval(exp));
	}
	
	public void testExcerciseEval() throws Exception {
		excerciseEval("(defvar (quote foo) 23)", "23");
		excerciseEval("foo", "23");		
	}

	public void testMacro() throws Exception {
		excerciseEval("(defvar 'w 99)", "99");
		excerciseEval("((lambdam () 'w) 45)", "99");
	}
	

	public void testMacroWithDefmacro() throws Exception {
		excerciseEval("(defmacro nil! (x) (list 'defvar (list quote x) 0))", "<anonymous macro>");
		excerciseEval("(nil! a)", "0");
		excerciseEval("a", "0");
	}

	public void testMacroWithDefmacroDeep() throws Exception {
		excerciseEval("(def fn (y) (defmacro nil! (x) (list 'defvar (list quote x) y)) nil!)", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(defvar 'm (fn 99))", "<anonymous macro>");
		excerciseEval("(m w)", "99");
		excerciseEval("w", "99");
	}

	public void testMacroWithDefmacroDeep2() throws Exception {
		excerciseEval("(defvar 'y 7777)", "7777");
		excerciseEval("(def fn () (defmacro mac (x) (list 'defvar (list quote x) y)) mac)", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(def fun (y) (defvar 'm (fn)) m)", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("((fun 5555) w)", "7777");
		excerciseEval("w", "7777");
	}

	public void testRecursion() throws Exception {
		excerciseEval("(defvar 'null (lambda (exp) (cond (exp nil) (t t))))", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(defvar 'last (lambda (x) (cond ((null (cdr x)) (car x)) (t (last (cdr x))))))", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(last '(1 2 3 4))", "4");

	}

	public void testLexicalScope() throws Exception {
		excerciseEval("(defvar 'x -1)", "-1");
		excerciseEval("(defvar 'mk-func (lambda (x) (lambda (y) (cons x y))))", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(mk-func 10)", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("((mk-func 10) 88)", "(10 : 88)");
	}

	public void testRestArgs() throws Exception {
		excerciseEval("(defvar 'fnq (lambdaq (x &rest body) body))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)");
		excerciseEval("(fnq foo bar 1 2)", "(bar 1 2)");
		
		excerciseEval("(defvar 'fnq (lambdam (x &rest body) body))", "<anonymous macro>");
		excerciseEval("(fnq 12 cons 1 2)", "(1 : 2)");
	}
	public void testFrame() throws Exception {
		excerciseEval("(dict (a 1) (b 2) (c 3))", "(dict (b 2) (c 3) (a 1))");
		excerciseEval("(equal (dict (a 1) (b 2) (c 3)) (dict (a 1) (b 2) (c 3)))", "t");
	}
	
	public void testEnvCapture() throws Exception {
		excerciseEval("(defvar 'mk-fn  (lambda (x) (defvar 'bal x) (defvar 'fn (lambda (y) (cons bal y))) fn))", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");    
		excerciseEval("(defvar 'ff (mk-fn 44))","<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(ff 99)", "(44 : 99)");
	}
	public void testEnvCaptureWithDef() throws Exception {
		excerciseEval("(def mk-fn (x) (defvar 'bal x) (def fn (y) (cons bal y)) fn)", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");    
		excerciseEval("(defvar 'ff (mk-fn 44))","<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(ff 99)", "(44 : 99)");
	}

	
	public void testDynamicVariablesWithDef() throws Exception {
		excerciseEval("(def function-which-declares-dynamic-var () (defvar '_x 88) (function-which-uses-dynamic-var))","<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(def function-which-uses-dynamic-var () (list _x _x))", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(function-which-declares-dynamic-var)","(88 88)");
		excerciseEval("_x","88");
	}

	public void testDynamicVariablesWithDef2() throws Exception {
		excerciseEval("(defvar '_x 11111)", "11111");
		excerciseEval("(def define-some-global-y (_x) (defvar '_y \"global _y\") (cons _x _y))", "<EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>");
		excerciseEval("(define-some-global-y 33)", "(11111 : \"global _y\")");
	}
}
