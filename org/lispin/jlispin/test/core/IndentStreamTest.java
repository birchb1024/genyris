package org.lispin.jlispin.test.core;

import junit.framework.TestCase;

import org.lispin.jlispin.core.ConvertEofInStream;
import org.lispin.jlispin.core.InStream;
import org.lispin.jlispin.core.IndentStream;
import org.lispin.jlispin.core.LexException;
import org.lispin.jlispin.core.StringInStream;

public class IndentStreamTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	private void excerciseIndent(String toparse, String expected) throws LexException {
		InStream ind = new ConvertEofInStream(new IndentStream(new StringInStream(toparse)));
		String result = "";
		while( ind.hasData() ) {
			result += ind.getChar();
		}
		assertEquals(expected, result);	
	}

	
	
	public void testIndentStream1() throws LexException {

		excerciseIndent("0 0 0 ;\n 1 1 1;\n  2 2 2;\n 1 1 1;\n0 0 0", "(0 0 0 (1 1 1(2 2 2))(1 1 1))(0 0 0)");
		excerciseIndent("0;\n 1;\n  2;\n 1;\n0", "(0(1(2))(1))(0)");
		excerciseIndent("0;\n 1;\n 1", "(0(1)(1))");
		excerciseIndent("0;\n 1;\n  2;\n 1;\n0", "(0(1(2))(1))(0)");
		excerciseIndent("0\n 1\n  2\n 1\n0", "(0(1(2))(1))(0)");
		excerciseIndent("0\n0\n", "(0)(0)");
		excerciseIndent("0\n 1\n 1", "(0(1)(1))");
		excerciseIndent("0;c\n 1;3\n;4", "(0(1))");
		excerciseIndent("0\n 1\n", "(0(1))");
		excerciseIndent("0", "(0)");
		excerciseIndent(";comment", "");
		excerciseIndent("", "");
		excerciseIndent("     ;comment", "");
		excerciseIndent("0000", "(0000)");
		excerciseIndent("0 1 ; 3 4", "(0 1 )");
		excerciseIndent("0 1 2 3 4", "(0 1 2 3 4)");
		excerciseIndent("\n0000", "(0000)");
	}

}
