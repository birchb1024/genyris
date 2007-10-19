package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

public class ClassTaggingTests extends TestCase {

	private TestUtilities interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new TestUtilities();
        interpreter._interpreter.init(true);
	}

	private void excerciseEval(String exp, String expected) throws Exception {
		assertEquals(expected, interpreter.eval(exp));
	}

    public void testDefineClass() throws Exception {
        excerciseEval("(class C)", "<class C () ()>");
        excerciseEval("(class C1 (C))" , "<class C1 (C) ()>");
        excerciseEval("C" , "<class C () (C1)>");
        }

    public void testTagWithColon() throws Exception {
		excerciseEval("(class Miles)", "<class Miles () ()>");
		excerciseEval("(define x 45)", "45");
        excerciseEval("(x:Miles)", "45");
        excerciseEval("(x _classes)", "(<class Miles () ()> (dict (_classname : Bignum)))");
       	}

    public void testTagWithTag() throws Exception {
        excerciseEval("(define Miles (dict(_classname: 'Miles)))", "(dict (_classname : Miles))");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(tag x Miles)", "45");
        excerciseEval("(x _classes)", "((dict (_classname : Miles)) (dict (_classname : Bignum)))");
        }
}
