package org.lispin.jlispin.test.io;

import junit.framework.TestCase;

import org.lispin.jlispin.io.ConvertEofInStream;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.IndentStream;
import org.lispin.jlispin.io.LexException;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class IndentStreamTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	private void excerciseIndent(String toparse, String expected) throws LexException {
		InStream ind = new ConvertEofInStream(new IndentStream(new UngettableInStream(new StringInStream(toparse)), false));
		String result = "";
		while( ind.hasData() ) {
			result += ind.readNext();
		}
		assertEquals(expected, result);	
	}
	
	private void excerciseIndentInteractive(String toparse, String expected) throws LexException {
		InStream ind = new ConvertEofInStream(new IndentStream(new UngettableInStream(new StringInStream(toparse)), true));
		String result = "";
		while( ind.hasData() ) {
			result += ind.readNext();
		}
		assertEquals(expected, result);	
	}

	public void testIndentCalc1() throws LexException {
		IndentStream ind = new IndentStream(new StringInStream("123"), false);
		assertEquals(1, ind.computeDepthFromSpaces(0));
		assertEquals(2, ind.computeDepthFromSpaces(1));
		assertEquals(3, ind.computeDepthFromSpaces(2));
		assertEquals(3, ind.computeDepthFromSpaces(2));
		assertEquals(3, ind.computeDepthFromSpaces(2));
	}
	
	public void testIndentCalc2() throws LexException {
		IndentStream ind = new IndentStream(new StringInStream("123"), false);
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
	public void testIndentStream18() throws LexException {
		excerciseIndentInteractive("1\n 2\n  3\n\n34", "(1(2(3)))(34)");
	}
	public void testIndentStream19() throws LexException {
		excerciseIndentInteractive("1\n 2\n ~ 3", "(1(2) 3)");
	}
	public void testIndentStream20() throws LexException {
		excerciseIndentInteractive("1\n 2\n ~3", "(1(2) 3)");
	}
	public void testIndentStream21() throws LexException {
		excerciseIndentInteractive("1\n 2\n  3\n ~ 22", "(1(2(3)) 22)");
	}
	public void testIndentStream22() throws LexException {
		excerciseIndentInteractive("~3", " 3");
	}
	public void testIndentStream23() throws LexException {
		excerciseIndentInteractive("~(3)", " (3)");
	}
	public void testIndentStream24() throws LexException {
		excerciseIndentInteractive("~(3 \n~ 4)", " (3  4)");
	}
	public void testIndentStream25() throws LexException {
		excerciseIndentInteractive("~1\n~2\n~3", " 1 2 3");
		//~1
		//~2
		//~3
	}
	public void testIndentStream26() throws LexException {
		excerciseIndentInteractive("foo\n bar\n ~1\n ~2\n~3", "(foo(bar) 1 2) 3");
		//foo
		// bar
		// ~1
		// ~2
		//~3
	}
	public void testIndentStream27() throws LexException {
		//			define 'fn
		//			  lambda (x)
		//			    cond
		//			      (eq nil (cdr x))
		//			         x
		//			      else
		//			         fn (cdr x)
		excerciseIndent(
				"defvar 'fn\n  lambda (x)\n    cond\n      (eq nil (cdr x))\n         x\n      else\n         fn (cdr x)\n"
				,"(defvar 'fn(lambda (x)(cond((eq nil (cdr x))(x))(else(fn (cdr x))))))");

	}
	public void testIndentStream28() throws LexException {
		excerciseIndent("\"foo\"", "(\"foo\")");
        excerciseIndent("quote\n  \"string\"\n", "(quote(\"string\"))");
	}

	public void testIndentMultiStatement() throws LexException {
		excerciseIndent("12\n34", "(12)(34)");
		excerciseIndent("12\n34\n56", "(12)(34)(56)");
		excerciseIndent("12\n 34\n56", "(12(34))(56)");
		excerciseIndent("12\n 34\n\n56", "(12(34))(56)");
		excerciseIndent("12\n 34\n\n~ 56", "(12(34)) 56");
		excerciseIndent("12\n 34\n ~ 56\n78", "(12(34) 56)(78)");
	}
}
