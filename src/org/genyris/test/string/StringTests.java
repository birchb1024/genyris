// Copyright 2008 Peter William Birch <birchb@genyis.org>
package org.genyris.test.string;

import junit.framework.TestCase;

import org.genyris.exception.GenyrisException;
import org.genyris.test.interp.TestUtilities;

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

    public void testStringToInts() throws GenyrisException {
        checkEval("(''(.toInts 'ASCII'))", "nil");
        checkEval("('0123456789'(.toInts 'ASCII'))", "(48 49 50 51 52 53 54 55 56 57)");
        checkEval("('1 2 3 4 5'(.toInts 'ASCII'))", "(49 32 50 32 51 32 52 32 53)");
        checkEval("('1,2,3,4,5'(.toInts 'ASCII'))", "(49 44 50 44 51 44 52 44 53)");
        checkEval("('abcdefghijklmnopqrstuvwxyz'(.toInts 'ASCII'))", "(97 98 99 100 101 102 103 104 105 106 107 108 109 110 111 112 113 114 115 116 117 118 119 120 121 122)");
        checkEval("('ABCDEFGHIJKLMNOPQRSTUVWXYZ'(.toInts 'ASCII'))", "(65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90)");
        checkEval("('!#$%&()*+,-./'(.toInts 'ASCII'))", "(33 35 36 37 38 40 41 42 43 44 45 46 47)");
        checkEval("('\r\n'(.toInts 'ASCII'))", "(13 10)");

    }
    public void testStringToIntsInternational() throws GenyrisException {
        checkEval("('0123456789'(.toInts 'GBK'))", "(48 49 50 51 52 53 54 55 56 57)");
        checkEval("('1 2 3 4 5'(.toInts 'UTF-16LE'))", "(49 0 32 0 50 0 32 0 51 0 32 0 52 0 32 0 53 0)");
        checkEval("('1,2,3,4,5'(.toInts 'UTF-8'))", "(49 44 50 44 51 44 52 44 53)");
        checkEval("('abcdefghijklmnopqrstuvwxyz'(.toInts 'UTF-16'))", "(254 255 0 97 0 98 0 99 0 100 0 101 0 102 0 103 0 104 0 105 0 106 0 107 0 108 0 109 0 110 0 111 0 112 0 113 0 114 0 115 0 116 0 117 0 118 0 119 0 120 0 121 0 122)");
        checkEval("('ABCDEFGHIJKLMNOPQRSTUVWXYZ'(.toInts 'ISO-8859-1'))", "(65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90)");
        checkEval("('!#$%&()*+,-./'(.toInts 'ISO-8859-1'))", "(33 35 36 37 38 40 41 42 43 44 45 46 47)");
        checkEval("('\r\n'(.toInts 'ISO-8859-1'))", "(13 10)");

    }
    public void testStringFromInts() throws GenyrisException {
        checkEvalBad("(String!fromInts 123)");
        checkEval("(String!fromInts 'ASCII' ^(48 49 50 51 52 53 54 55 56 57))", "'0123456789'");
        checkEval("(String!fromInts 'ASCII' ^(49 32 50 32 51 32 52 32 53))", "'1 2 3 4 5'");
        checkEval("(String!fromInts 'ASCII' ^(49 44 50 44 51 44 52 44 53))", "'1,2,3,4,5'");
        checkEval("(String!fromInts 'ASCII' ^(97 98 99 100 101 102 103 104 105 106 107 108 109 110 111 112 113 114 115 116 117 118 119 120 121 122))", "'abcdefghijklmnopqrstuvwxyz'");
        checkEval("(String!fromInts 'ASCII' ^(65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90))", "'ABCDEFGHIJKLMNOPQRSTUVWXYZ'");
        checkEval("(String!fromInts 'ASCII' ^(33 35 36 37 38 40 41 42 43 44 45 46 47))", "'!#$%&()*+,-./'");
        checkEval("(String!fromInts 'ASCII' ^(13 10))", "'\\r\\n'");
    }
    public void testStringToFromASCIIInts() throws GenyrisException {
    	for(int i = 0 ; i< 128;i++) {
            checkEval("((String!fromInts 'ASCII' ^(" + i + "))(.toInts 'ASCII'))", "(" + i + ")");
    	}
    }
    public void testStringToFromUTF8Ints() throws GenyrisException {
    	for(int i = 0 ; i< 128;i++) {
            checkEval("((String!fromInts 'UTF-8' ^(" + i + "))(.toInts 'UTF-8'))", "(" + i + ")");
    	}
    }
    
    public void testStringReplace() throws GenyrisException {
        checkEval("(''(.replace 'a' 's'))", "''");
        checkEval("('1 2 3 4 5'(.replace '1' 'x'))", "'x 2 3 4 5'");
        checkEval("('1 2 3 4 5'(.replace '1 2 3' 'xyz'))", "'xyz 4 5'");
        checkEval("('1 2 3 1 2 3'(.replace '1 2 3' 'xyz'))", "'xyz xyz'");
        checkEvalBad("('x'(.replace) '' 'a')");
        checkEvalBad("(23(.replace) 4 5)");
    }

    public void testStringSlice() throws GenyrisException {
        checkEvalBad("(''(.slice 0 1))");
        checkEval("('012'(.slice 0 0))", "'0'");
        checkEval("('012'(.slice 0 1))", "'01'");
        checkEval("('012'(.slice 1 2))", "'12'");
        checkEval("('012'(.slice 0 2))", "'012'");
        checkEval("('012'(.slice 1 -1))", "'12'");
    }
    public void testStringSliceBase64() throws GenyrisException {
    	checkEval("(('TlRMTVNTUAABAAAAB7IIogcABwA1AAAADQANACgAAAAFASgKAAAAD1cwMDFFNEZGMDQ5M0JPQ0VBTklB'(.fromBase64))(.slice 0 6))","'NTLMSSP'");
    	checkEval("(('TlRMTVNTUAABAAAAB7IIogcABwA1AAAADQANACgAAAAFASgKAAAAD1cwMDFFNEZGMDQ5M0JPQ0VBTklB'(.fromBase64))(.length))","60");
    	checkEval("(('TlRMTVNTUAABAAAAB7IIogcABwA1AAAADQANACgAAAAFASgKAAAAD1cwMDFFNEZGMDQ5M0JPQ0VBTklB'(.fromBase64))(.slice (- 59 6) 59))","'OCEANIA'");

    }
    public void testStringLength() throws GenyrisException {
        checkEval("('34'(.length))", "2");
        checkEvalBad("(34(.length))");
    }
    public void testStringConcat() throws GenyrisException {
        checkEval("(''(.+))", "''");
        checkEval("('A'(.+))", "'A'");
        checkEval("('A'(.+ 'B'))", "'AB'");
        checkEval("('A'(.+ 'B' 'C'))", "'ABC'");
        checkEvalBad("('A'(.+ 55))");
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
        checkEvalBad("(^qweqwe(.toLowerCase))");
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

    public void testBase64() throws GenyrisException {
    	checkEval("(''(.fromBase64))", "''");
    	checkEval("(''(.toBase64))", "''");
        checkEval("(':'(.toBase64))", "'Og=='");
    	checkEval("('Zm9vOmJhcg=='(.fromBase64))", "'foo:bar'");
        checkEval("('foo:bar'(.toBase64))", "'Zm9vOmJhcg=='");
        checkEval("('zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz'(.toBase64))", "'enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6enp6eno='");
    }
        
}
