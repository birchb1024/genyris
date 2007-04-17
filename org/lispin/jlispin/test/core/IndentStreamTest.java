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
		excerciseIndent("", "");
		excerciseIndent("0", "(0)");
		excerciseIndent("0000", "(0000)");
		excerciseIndent("0\n 1\n", "(0(1))");
	}

}
