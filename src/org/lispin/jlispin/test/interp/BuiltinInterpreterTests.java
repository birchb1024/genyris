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

public class BuiltinInterpreterTests extends TestCase {
	
	private Interpreter interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new Interpreter();
	}
	
	void excerciseEval(String exp, String expected) throws Exception {
		InStream input = new UngettableInStream( new StringInStream(exp));
		Parser parser = interpreter.newParser(input);
		Exp expression = parser.read(); 
		Exp result = interpreter.evalInGlobalEnvironment(expression);
		
		StringWriter out = new StringWriter();
		BasicFormatter formatter = new BasicFormatter(out);
		result.acceptVisitor(formatter);
		assertEquals(expected, out.getBuffer().toString());	
	}
	
	public void testExcerciseEval() throws Exception {
		excerciseEval("(defvar (quote foo) 23)", "23");
		excerciseEval("foo", "23");		
	}

	public void testEquality() throws Exception {
		excerciseEval("(equal 1 1)", "true");
		excerciseEval("(equal 1.2e4 1.2e4)", "true");
		excerciseEval("(equal \"foo\" \"foo\")", "true");
		excerciseEval("(equal 'sym 'sym)", "true");
	}
	public void testEqu() throws Exception {
		excerciseEval("(defvar 'var 23)", "23");
		excerciseEval("(eq 1 1)", "nil");
		excerciseEval("(eq 1.2e4 1.2e4)", "nil");
		excerciseEval("(eq \"foo\" \"foo\")", "nil");
		excerciseEval("(eq 'sym 'sym)", "true");
		excerciseEval("(eq var var)", "true");
	}
    public void testDict() throws Exception {
        excerciseEval("(dict (a : 1) (b : 2))","(dict (b : 2) (a : 1))");    
        excerciseEval("(dict (a) (b : 2))", "(dict (b : 2) (a : nil))");    
        excerciseEval("(dict (a : '(1)) (b : 2))", "(dict (b : 2) (a : (1)))");    
    } 

}