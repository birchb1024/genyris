package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

public class ComplexInterpreterTests extends TestCase {
	
	private TestUtilities interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new TestUtilities();
	}
	
	private void excerciseEval(String exp, String expected) throws Exception {
		assertEquals(interpreter.eval(exp), expected);
	}
	
	public void testExcerciseEval() throws Exception {
		excerciseEval("(defvar (quote foo) 23)", "23");
		excerciseEval("foo", "23");		
	}

	public void testMacro() throws Exception {
		excerciseEval("(defvar 'w 99)", "99");
		excerciseEval("((lambdam () 'w) 45)", "99");
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
		excerciseEval("((mk-func 10) 88)", "(10 ^ 88)");
	}

	public void testRestArgs() throws Exception {
		excerciseEval("(defvar 'fnq (lambdaq (x &rest body) body))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(fnq 1 2 3 4 5 6)", "(2 3 4 5 6)");
		excerciseEval("(fnq foo bar 1 2)", "(bar 1 2)");
		
		excerciseEval("(defvar 'fnq (lambdam (x &rest body) body))", "<anonymous macro>");
		excerciseEval("(fnq 12 cons 1 2)", "(1 ^ 2)");
	}
	public void testFrame() throws Exception {
		excerciseEval("(dict (a 1) (b 2) (c 3))", "<CallableEnvironment<dict ((b 2) (c 3) (a 1))>>");
		excerciseEval("(equal (dict (a 1) (b 2) (c 3)) (dict (a 1) (b 2) (c 3)))", "t");
	}
}
