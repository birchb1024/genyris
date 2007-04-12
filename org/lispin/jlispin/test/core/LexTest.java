package org.lispin.jlispin.test.core;

import org.lispin.jlispin.core.*;

import junit.framework.TestCase;

public class LexTest extends TestCase {
	
	public void testLpower() throws Exception {
		assertEquals(1, Lex.lpower(1, 2), 0.01);
		assertEquals(8, Lex.lpower(2, 3), 0.01);
		assertEquals(5.29, Lex.lpower(2.3, 2), 0.01);		
	}
	
	public void testNumber() throws Exception {
		InStream in1 = new UngettableInStream( 
				new StringInStream("1234567"));
		Lex lexer = new Lex(in1);
		assertEquals(0, lexer.asciiToInt('0'));
		assertEquals(1, lexer.asciiToInt('1'));
		assertEquals(2, lexer.asciiToInt('2'));	
		assertEquals(3, lexer.asciiToInt('3'));
		assertEquals(4, lexer.asciiToInt('4'));
		assertEquals(5, lexer.asciiToInt('5'));
		assertEquals(6, lexer.asciiToInt('6'));
		assertEquals(7, lexer.asciiToInt('7'));
		assertEquals(8, lexer.asciiToInt('8'));
		assertEquals(9, lexer.asciiToInt('9'));
		assertEquals(888, lexer.asciiToInt('.'));
		assertEquals('.', in1.lgetc());
		assertEquals(999, lexer.asciiToInt('e'));
		assertEquals('e', in1.lgetc());
		assertEquals(999, lexer.asciiToInt('E'));
		assertEquals('E', in1.lgetc());
		assertEquals(100, lexer.asciiToInt('?'));
		assertEquals('?', in1.lgetc());
		}

}
