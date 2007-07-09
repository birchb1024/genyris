package org.lispin.jlispin.test.core;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.format.BasicFormatter;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Lex;
import org.lispin.jlispin.io.LexException;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

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
		assertEquals(expected.toString(), lexer.nextToken().toString());		
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

		excerciseNextTokenExp(new Lsymbol("foo"), "foo");
		excerciseNextTokenExp(new Lsymbol("foo*bar"), "foo\\*bar");
		excerciseNextTokenExp(new Lsymbol("quux"), "\n\nquux");
		excerciseNextTokenExp(new Lsymbol("|123|"), "  \t|123|");
		excerciseNextTokenExp(new Lsymbol("_x"), "  \t _x");

	}
	public void testLexIdentMinus() throws Exception {
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
		assertEquals(new Linteger(12), lexer.nextToken());					
		assertEquals(new Lsymbol("double").getJavaValue(), lexer.nextToken().getJavaValue());					
		assertEquals(new Ldouble(12.34), lexer.nextToken());					
		assertEquals(new Ldouble(-12.34e5), lexer.nextToken());	
		assertEquals(new Lstring("string"), lexer.nextToken());	
		
	}

	private void excerciseListParsing(String toParse) throws Exception {
		SymbolTable table = new SymbolTable();
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

		excerciseListParsing("(1 ^ 2)"); 
		excerciseListParsing("(1 2 ^ 3)");	
		
		excerciseListParsing("(\"a\" 1.2 30000 foo)"); 
		excerciseListParsing("(\"a\" 1.2 30000 foo (1 2 ^ 3))"); 
		excerciseListParsing("(\"a\" 1.2 30000 foo (1 2 ^ 3) (1 (2) 3 (4 (5 (6)))))");		
		excerciseListParsing("(defun my-func (x) (cons x x))");	
	}

	private void excerciseSpecialParsing(String toParse, String expected) throws Exception {
		SymbolTable table = new SymbolTable();
		InStream input = new UngettableInStream( new StringInStream(toParse));
		Parser parser = new Parser(table, input);
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
		excerciseSpecialParsing("'(1 ^ 2)", "(quote (1 ^ 2))"); 
	}

	public void testSpecialLexBackQuote() throws Exception {
		excerciseSpecialParsing("(`a)", "(backquote a)");
		excerciseSpecialParsing("(`12.34)", "(backquote 12.34)");
		excerciseSpecialParsing("(`\"str\")", "(backquote \"str\")");
		excerciseSpecialParsing("(`(1 2))", "(backquote (1 2))");
		excerciseSpecialParsing("(`(1 ^ 2))", "(backquote (1 ^ 2))");
		excerciseSpecialParsing("(``(1 ^ 2))", "(backquote backquote (1 ^ 2))");
		excerciseSpecialParsing("(`(1 ^ 2)`)", "(backquote (1 ^ 2) backquote)");
	}

	public void testSpecialLexComma() throws Exception {
		excerciseSpecialParsing(",a", "(comma a)");
	}

	public void testSpecialLexCommaAt() throws Exception {
		excerciseSpecialParsing(",@12", "(comma-at 12)");
	}
	
}
