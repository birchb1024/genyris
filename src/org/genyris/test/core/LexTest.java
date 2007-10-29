// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import java.io.StringWriter;
import junit.framework.TestCase;
import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.format.BasicFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.InStream;
import org.genyris.io.Lex;
import org.genyris.io.LexException;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

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

	private void excerciseNextTokenBignum(Exp expected, String toparse) throws LexException {
		Lex lexer = new Lex(new UngettableInStream( new StringInStream(toparse)), _table);
		assertEquals(expected.getJavaValue().toString(), lexer.nextToken().getJavaValue().toString());
	}
	private void excerciseNextTokenExp(Exp expected, String toparse) throws LexException {
		Lex lexer = new Lex(new UngettableInStream( new StringInStream(toparse)), _table);
		assertEquals(expected.toString(), lexer.nextToken().toString());
	}

	public void testLex1() throws Exception {

		excerciseNextTokenInt(new Bignum(12), "12");
		excerciseNextTokenInt(new Bignum(-12), "-12");
		excerciseNextTokenBignum(new Bignum("12.34"), "12.34");
		excerciseNextTokenBignum(new Bignum("-12.34"), "-12.34");
		excerciseNextTokenDouble(new Ldouble(12.34e5), "12.34e5");
		excerciseNextTokenDouble(new Ldouble(-12.34e-5), "-12.34e-5");
		excerciseNextTokenDouble(new Ldouble(-12e-5), "-12.0e-5");
	}

	public void testLex2() throws Exception {

		excerciseNextTokenInt(new Bignum(12), "   12");
		excerciseNextTokenInt(new Bignum(-12), "\t\t-12");
		excerciseNextTokenBignum(new Bignum("12.34"), "\n\n12.34");
		excerciseNextTokenBignum(new Bignum("-12.34"), "\r\f -12.34");

	}

	public void testLexIdent1() throws Exception {

		excerciseNextTokenExp(new Lsymbol("foo"), "foo");
		excerciseNextTokenExp(new Lsymbol("foo*bar"), "foo\\*bar");
		excerciseNextTokenExp(new Lsymbol("quux"), "\n\nquux");
		excerciseNextTokenExp(new Lsymbol("|123|"), "  \t|123|");
		excerciseNextTokenExp(new Lsymbol("_x"), "  \t _x");

	}
	public void testLexIdentMinus() throws Exception {
		excerciseNextTokenExp(new Lsymbol("-"), "- 3");
		excerciseNextTokenExp(new Lsymbol("-f"), "-f");
		excerciseNextTokenExp(new Lsymbol("--"), "--");
	}
	public void testLexCommentStrip() throws Exception {
		excerciseNextTokenExp(new Lsymbol("X"), "X ; foo");
		excerciseNextTokenExp(new Lsymbol("Y"), "; stripped \nY");
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

	public void testCombination1() throws Exception {
		Lex lexer = new Lex(new UngettableInStream( new StringInStream("int 12 double\n 12.34\r\n -12.34e5 \"string\" ")), _table);
		assertEquals(new Lsymbol("int").getJavaValue(), lexer.nextToken().getJavaValue());
		assertEquals(new Bignum(12), lexer.nextToken());
		assertEquals(new Lsymbol("double").getJavaValue(), lexer.nextToken().getJavaValue());
		assertEquals(new Bignum("12.34"), lexer.nextToken());
		assertEquals(new Ldouble(-12.34e5), lexer.nextToken());
		assertEquals(new Lstring("string"), lexer.nextToken());

	}

	private void excerciseListParsing(String toParse) throws Exception {
		Lsymbol NIL = new NilSymbol();
		SymbolTable table = new SymbolTable();
		table.init(NIL);
		InStream input = new UngettableInStream( new StringInStream(toParse));
		Parser parser = new Parser(table, input);
		Exp result = parser.read();

		StringWriter out = new StringWriter();
		BasicFormatter formatter = new BasicFormatter(out);
		result.acceptVisitor(formatter);
		assertEquals(toParse, out.getBuffer().toString());

	}



	public void testLists1() throws Exception {
		excerciseListParsing("(1 2 3)");
		excerciseListParsing("(1 (2) 3)");
		excerciseListParsing("(1 (2) 3 (4 (5 (6))))");

		excerciseListParsing("(1 : 2)");
		excerciseListParsing("(1 2 : 3)");

		excerciseListParsing("(\"a\" 1.2 30000 foo)");
		excerciseListParsing("(\"a\" 1.2 30000 foo (1 2 : 3))");
		excerciseListParsing("(\"a\" 1.2 30000 foo (1 2 : 3) (1 (2) 3 (4 (5 (6)))))");
		excerciseListParsing("(defun my-func (x) (cons x x))");
	}

	private void excerciseSpecialParsing(String toParse, String expected) throws Exception {
        Interpreter interpreter = new Interpreter();
        interpreter.init(true);

		InStream input = new UngettableInStream( new StringInStream(toParse));
		Parser parser = new Parser(interpreter.getSymbolTable(), input);
		Exp result = parser.read();

		StringWriter out = new StringWriter();
		BasicFormatter formatter = new BasicFormatter(out);
		result.acceptVisitor(formatter);
		assertEquals(expected, out.getBuffer().toString());

	}

	public void testSpecialLexQuote() throws Exception {
		excerciseSpecialParsing("'a", "(quote a)");
		excerciseSpecialParsing("'12.34", "(quote 12.34)");
		excerciseSpecialParsing("'\"str\"", "(quote \"str\")");
		excerciseSpecialParsing("'(1 2)", "(quote (1 2))");
		excerciseSpecialParsing("'(1 : 2)", "(quote (1 : 2))");
        // excerciseSpecialParsing("'(: 2)", "(quote (1 : 2))");
        // excerciseSpecialParsing("'(:)", "(quote (1 : 2))");
        excerciseSpecialParsing(":", "pair-delimiter");
        excerciseSpecialParsing("'(1:2)", "(quote (1 : 2))");
        excerciseSpecialParsing("'(1 :2)", "(quote (1 : 2))");
        excerciseSpecialParsing("'(a:b)", "(quote (a : b))");
        excerciseSpecialParsing("'(a :b)", "(quote (a : b))");
        excerciseSpecialParsing("'(a: b)", "(quote (a : b))");
        excerciseSpecialParsing("'(a: (3))", "(quote (a : (3)))");
	}

	public void testSpecialLexBackQuote() throws Exception {
		excerciseSpecialParsing("(`a)", "(backquote a)");
		excerciseSpecialParsing("(`12.34)", "(backquote 12.34)");
		excerciseSpecialParsing("(`\"str\")", "(backquote \"str\")");
		excerciseSpecialParsing("(`(1 2))", "(backquote (1 2))");
		excerciseSpecialParsing("(`(1 : 2))", "(backquote (1 : 2))");
		excerciseSpecialParsing("(``(1 : 2))", "(backquote backquote (1 : 2))");
		excerciseSpecialParsing("(`(1 : 2)`)", "(backquote (1 : 2) backquote)");
	}

	public void testSpecialLexComma() throws Exception {
		excerciseSpecialParsing(",a", "(comma a)");
	}

	public void testSpecialLexCommaAt() throws Exception {
		excerciseSpecialParsing(",@12", "(comma-at 12)");
	}

}
