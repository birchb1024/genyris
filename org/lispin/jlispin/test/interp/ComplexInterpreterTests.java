package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.InStream;
import org.lispin.jlispin.core.Parser;
import org.lispin.jlispin.core.StringInStream;
import org.lispin.jlispin.core.UngettableInStream;
import org.lispin.jlispin.interp.Interpreter;

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


}
