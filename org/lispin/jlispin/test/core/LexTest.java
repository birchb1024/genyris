package org.lispin.jlispin.test.core;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Lex;
import org.lispin.jlispin.core.LexException;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.StringInStream;
import org.lispin.jlispin.core.Symbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.UngettableInStream;

import junit.framework.TestCase;

public class LexTest extends TestCase {
	
	public SymbolTable _table = new SymbolTable();
	
	private void excerciseNextTokenInt(Exp expected, String toparse) throws LexException {
		Lex lexer = new Lex(new UngettableInStream( new StringInStream(toparse)), _table);
		assertEquals(expected, lexer.nextToken());		
	}
	
	private void excerciseNextTokenDouble(Exp expected, String toparse) throws LexException {
		Lex lexer = new Lex(new UngettableInStream( new StringInStream(toparse)), _table);
		assertEquals(((Double)expected.getJavaValue()).doubleValue(), ((Double)lexer.nextToken().getJavaValue()).doubleValue(), 0.00001);		
	}

	private void excerciseNextTokenExp(Exp expected, String toparse) throws LexException {
		Lex lexer = new Lex(new UngettableInStream( new StringInStream(toparse)), _table);
		assertEquals(expected, lexer.nextToken());		
	}

	public void testLex1() throws Exception {

		excerciseNextTokenInt(new Linteger(12), "12");
		excerciseNextTokenInt(new Linteger(-12), "-12");
		excerciseNextTokenDouble(new Ldouble(12.34), "12.34");
		excerciseNextTokenDouble(new Ldouble(-12.34), "-12.34");
		excerciseNextTokenDouble(new Ldouble(12.34e5), "12.34e5");
		excerciseNextTokenDouble(new Ldouble(-12.34e-5), "-12.34e-5");
		excerciseNextTokenDouble(new Ldouble(-12e-5), "-12.0e-5");		
	}

	public void testLex2() throws Exception {

		excerciseNextTokenInt(new Linteger(12), "   12");
		excerciseNextTokenInt(new Linteger(-12), "\t\t-12");
		excerciseNextTokenDouble(new Ldouble(12.34), "\n\n12.34");
		excerciseNextTokenDouble(new Ldouble(-12.34), "\r\f -12.34");

	}

	public void testLexIdent1() throws Exception {

		excerciseNextTokenExp(new Symbol("foo"), "foo");
		excerciseNextTokenExp(new Symbol("foo*bar"), "foo\\*bar");
		excerciseNextTokenExp(new Symbol("quux"), "\n\nquux");
		excerciseNextTokenExp(new Symbol("|123|"), "  \t|123|");

	}
	public void testLexIdentMinus() throws Exception {
		excerciseNextTokenExp(new Symbol("-f"), "-f");
		excerciseNextTokenExp(new Symbol("--"), "--");
	}
	public void testLexCommentStrip() throws Exception {
		excerciseNextTokenExp(new Symbol("X"), "X ; foo");
		excerciseNextTokenExp(new Symbol("Y"), "; stripped \nY");
		excerciseNextTokenExp(new Linteger(12), "   \n\t\f      ; stripped \n12");
		}


	public void testLexString() throws Exception {
		excerciseNextTokenExp(new Lstring("str"), "\"str\"");
		excerciseNextTokenExp(new Lstring("s\nr"), "\"s\nr\"");
		excerciseNextTokenExp(new Lstring("s\nr"), "\"s\nr\"");
		excerciseNextTokenExp(new Lstring("s\nr"), "\"s\nr\"");
		excerciseNextTokenExp(new Lstring("\n\t\f\r\\"), "\"\n\t\f\r\\\\\"");
		excerciseNextTokenExp(new Lstring("a1-"), "\"\\a\\1\\-\"");
	}


}
