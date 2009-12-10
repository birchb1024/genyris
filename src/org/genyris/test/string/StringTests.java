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

    public void testStringSplit() throws GenyrisException {
        checkEval("(''(.split))", "('')");
        checkEval("('1 2 3 4 5'(.split))", "('1' '2' '3' '4' '5')");
        checkEval("('1,2,3,4,5'(.split ','))", "('1' '2' '3' '4' '5')");
        checkEval("('1    2 \t3  4 5'(.split '[ \\t]+'))", "('1' '2' '3' '4' '5')");

        checkEvalBad("((String.split) 'A' 'B')");

        checkEval("('http://www.genyris.org/path/index.html'(.split 'http://'))", "('' 'www.genyris.org/path/index.html')");

    }

    public void testStringConcat() throws GenyrisException {
        checkEval("(''(.+))", "''");
        checkEval("('A'(.+))", "'A'");
        checkEval("('A'(.+ 'B'))", "'AB'");
        checkEval("('A'(.+ 'B' 'C'))", "'ABC'");
    }
    public void testStringMatch() throws GenyrisException {
        checkEval("('abc'(.match 'a.c'))", "true");
        checkEval("('abc'(.match 'a.d'))", "nil");
        checkEval("(''(.match ''))", "true");
        checkEvalBad("(3(.match ''))");
        checkEvalBad("('A'(.match))");
        checkEvalBad("('A'(.match 34))");
        checkEval("('http://www.genyris.org/path/index.html'(.match 'http://[^/]+/.*'))", "true");
    }
    public void testStringToLowerCase() throws GenyrisException {
    	checkEval("(''(.toLowerCase))", "''");
        checkEval("('ABCDEFGH'(.toLowerCase))", "'abcdefgh'");
    }
    
    public void testFormat() throws GenyrisException {
    	checkEval("(''(.format))", "''");
    	checkEval("('%s'(.format))", "'%s'");
    	checkEvalBad("('%s%s'(.format 9))");
    	checkEvalBad("('%s%s'(.format 9 10 11))");
    	checkEval("('a=%s b=%s'(.format (+ 3 4) (* 7 8)))", "'a=7 b=56'");
    	checkEval("('a=%a b=%s %n'(.format 'x' (* 7 8)))", "'a=x b=56 \\n'");
    	checkEval("('%u'(.format 'A @!#$^%@#$&'))", "'A+%40%21%23%24%5E%25%40%23%24%26'");
    }

    
}
