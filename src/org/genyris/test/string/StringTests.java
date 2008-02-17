// Copyright 2008 Peter William Birch <birchb@genyis.org>
package org.genyris.test.string;

import org.genyris.exception.GenyrisException;
import org.genyris.test.interp.TestUtilities;

import junit.framework.TestCase;

public class StringTests extends TestCase {

	private TestUtilities interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new TestUtilities();
	}

	private void checkEval(String exp, String expected) throws GenyrisException {
		assertEquals(expected, interpreter.eval(exp));
	}

	private void checkEvalBad(String exp) throws GenyrisException {
		try {
			interpreter.eval(exp);
			fail("expecting exception");
		} catch (GenyrisException e) {
		}
	}

	private void eval(String script) throws GenyrisException {
		interpreter.eval(script);
	}

	public void testStringSplit() throws GenyrisException {
		checkEval("(\"\"(_split))", "(\"\")");
		checkEval("(\"1 2 3 4 5\"(_split))", "(\"1\" \"2\" \"3\" \"4\" \"5\")");
		checkEval("(\"1,2,3,4,5\"(_split \",\"))", "(\"1\" \"2\" \"3\" \"4\" \"5\")");
		checkEval("(\"1    2 \t3  4 5\"(_split \"[ \\t]+\"))", "(\"1\" \"2\" \"3\" \"4\" \"5\")");

		checkEvalBad("((String_split) \"A\" \"B\")");

		checkEval("(\"http://www.genyris.org/path/index.html\"(_split \"http://\"))", "(\"\" \"www.genyris.org/path/index.html\")");

	}

	public void testStringConcat() throws GenyrisException {
		checkEval("(\"\"(_+))", "\"\"");
		checkEval("(\"A\"(_+))", "\"A\"");
		checkEval("(\"A\"(_+ \"B\"))", "\"AB\"");
		checkEval("(\"A\"(_+ \"B\" \"C\"))", "\"ABC\"");
	}
	public void testStringMatch() throws GenyrisException {
		checkEval("(\"abc\"(_match \"a.c\"))", "true");
		checkEval("(\"abc\"(_match \"a.d\"))", "nil");
		checkEval("(\"\"(_match \"\"))", "true");
		checkEvalBad("(3(_match \"\"))");
		checkEvalBad("(\"A\"(_match))");
		checkEvalBad("(\"A\"(_match 34))");
		checkEval("(\"http://www.genyris.org/path/index.html\"(_match \"http://[^/]+/.*\"))", "true");
	}

}