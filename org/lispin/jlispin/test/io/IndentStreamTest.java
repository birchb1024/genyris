package org.lispin.jlispin.test.io;

import junit.framework.TestCase;

import org.lispin.jlispin.io.ConvertEofInStream;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.IndentStream;
import org.lispin.jlispin.io.LexException;
import org.lispin.jlispin.io.StringInStream;

public class IndentStreamTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	private void excerciseIndent(String toparse, String expected) throws LexException {
		InStream ind = new ConvertEofInStream(new IndentStream(new StringInStream(toparse)));
		String result = "";
		while( ind.hasData() ) {
			result += ind.readNext();
		}
		assertEquals(expected, result);	
	}
	
	public void testIndentCalc1() throws LexException {
		IndentStream ind = new IndentStream(new StringInStream("123"));
		assertEquals(1, ind.computeDepthFromSpaces(0));
		assertEquals(2, ind.computeDepthFromSpaces(1));
		assertEquals(3, ind.computeDepthFromSpaces(2));
		assertEquals(3, ind.computeDepthFromSpaces(2));
		assertEquals(3, ind.computeDepthFromSpaces(2));
	}
	
	public void testIndentCalc2() throws LexException {
		IndentStream ind = new IndentStream(new StringInStream("123"));
		assertEquals(1, ind.computeDepthFromSpaces(0));
		assertEquals(2, ind.computeDepthFromSpaces(1));
		assertEquals(2, ind.computeDepthFromSpaces(1));
		assertEquals(2, ind.computeDepthFromSpaces(1));
		assertEquals(2, ind.computeDepthFromSpaces(1));
	}

	public void testIndentStream1() throws LexException {
		excerciseIndent("0\n 1", "(0(1))");
	}
	public void testIndentStream2() throws LexException {
		excerciseIndent("0 0 0 ;\n 1 1 1;\n  2 2 2;\n 1 1 1;\n0 0 0", "(0 0 0 (1 1 1(2 2 2))(1 1 1))(0 0 0)");
	}
	public void testIndentStream3() throws LexException {
		excerciseIndent("0;\n 1;\n  2;\n 1;\n0", "(0(1(2))(1))(0)");
	}
	public void testIndentStream4() throws LexException {
		excerciseIndent("0;\n 1;\n 1", "(0(1)(1))");
	}
	public void testIndentStream5() throws LexException {
		excerciseIndent("0;\n 1;\n  2;\n 1;\n0", "(0(1(2))(1))(0)");
	}
	public void testIndentStream6() throws LexException {
		excerciseIndent("0\n 1\n  2\n 1\n0", "(0(1(2))(1))(0)");
	}
	public void testIndentStream7() throws LexException {
		excerciseIndent("0\n0\n", "(0)(0)");
	}
	public void testIndentStream8() throws LexException {
		excerciseIndent("0\n 1\n 1", "(0(1)(1))");
	}
	public void testIndentStream9() throws LexException {
		excerciseIndent("0;c\n 1;3\n;4", "(0(1))");
	}
	public void testIndentStream10() throws LexException {
		excerciseIndent("0", "(0)");
	}
	public void testIndentStream11() throws LexException {
		excerciseIndent(";comment", "");
	}
	public void testIndentStream12() throws LexException {
		excerciseIndent("", "");
	}
	public void testIndentStream13() throws LexException {
		excerciseIndent("     ;comment", "");
	}
	public void testIndentStream14() throws LexException {
		excerciseIndent("0000", "(0000)");
		}
	public void testIndentStream15() throws LexException {
		excerciseIndent("0 1 ; 3 4", "(0 1 )");
	}
	public void testIndentStream16() throws LexException {
		excerciseIndent("0 1 2 3 4", "(0 1 2 3 4)");
	}
	public void testIndentStream17() throws LexException {
		excerciseIndent("\n0000", "(0000)");
	}
	

}
