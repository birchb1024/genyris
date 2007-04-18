package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class ComplexInterpreterTests extends TestCase {
	
	private Interpreter interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new Interpreter();
	}
	
	void excerciseEval(String exp, String expected) throws Exception {
		InStream input = new UngettableInStream( new StringInStream(exp));
		Parser parser = interpreter.newParser(input);
		Exp expression = parser.read(); 
		Exp result = interpreter.eval(expression);
		assertEquals(expected, result.toString());
	}
	
	public void testExcerciseEval() throws Exception {
		excerciseEval("(define (quote foo) 23)", "23");
		excerciseEval("foo", "23");		
	}

	public void testMacro() throws Exception {
		excerciseEval("(define 'w 99)", "99");
		excerciseEval("((lambdam () 'w) 45)", "99");
	}

	public void testRecursion() throws Exception {
		excerciseEval("(define 'null (lambda (exp) (cond (exp nil) (t t))))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(define 'last (lambda (x) (cond ((null (cdr x)) (car x)) (t (last (cdr x))))))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(last '(1 2 3 4))", "4");

	}

	public void testLexicalScope() throws Exception {
		excerciseEval("(define 'x -1)", "-1");
		excerciseEval("(define 'mk-func (lambda (x) (lambda (y) (cons x y))))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(mk-func 10)", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("((mk-func 10) 88)", "(10 . 88)");
	}

}
