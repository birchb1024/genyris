package org.lispin.jlispin.test.interp;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.format.BasicFormatter;
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
		
		StringWriter out = new StringWriter();
		BasicFormatter formatter = new BasicFormatter(out);
		result.acceptVisitor(formatter);
		assertEquals(expected, out.getBuffer().toString());	
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
		excerciseEval("(defvar 'null (lambda (exp) (cond (exp nil) (t t))))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(defvar 'last (lambda (x) (cond ((null (cdr x)) (car x)) (t (last (cdr x))))))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(last '(1 2 3 4))", "4");

	}

	public void testLexicalScope() throws Exception {
		excerciseEval("(defvar 'x -1)", "-1");
		excerciseEval("(defvar 'mk-func (lambda (x) (lambda (y) (cons x y))))", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("(mk-func 10)", "<org.lispin.jlispin.interp.ClassicFunction>");
		excerciseEval("((mk-func 10) 88)", "(10 . 88)");
	}

}
