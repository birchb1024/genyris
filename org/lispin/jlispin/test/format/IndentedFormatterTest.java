package org.lispin.jlispin.test.format;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.format.IndentedFormatter;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class IndentedFormatterTest extends TestCase {

	void excerciseFormatter(String given, String expected, int depth) throws Exception { 
		Interpreter interpreter = new Interpreter();
		InStream input = new UngettableInStream( new StringInStream(given));
		Parser parser = interpreter.newParser(input);
		Exp expression = parser.read(); 
		StringWriter out = new StringWriter();
		IndentedFormatter formatter = new IndentedFormatter(out, depth);
		
		expression.acceptVisitor(formatter);
		
		assertEquals(expected, out.getBuffer().toString());
	}

	public void testAtom1() throws Exception {
		excerciseFormatter("12", "~ 12", 2);	
	} 
	public void testAtom2() throws Exception {
		excerciseFormatter("nil", "~ nil", 2);	
	} 
	public void testAtom3() throws Exception {
		excerciseFormatter("1.23E15", "~ 1.23E15", 2);	
	} 
	public void testAtom4() throws Exception {
		excerciseFormatter("foo", "~ foo", 2);	
	} 
	public void testAtom5() throws Exception {
		excerciseFormatter("\"string\"", "~ \"string\"", 2);	
	} 
	public void test1() throws Exception {
		excerciseFormatter("(1 2.3 \"str\" (symbol))", "1 2.3 \"str\"\n   symbol", 2);	
	} 
	public void test2() throws Exception {
		excerciseFormatter("(1 2.3 (nil nil 23 45 . 89))", "1 2.3\n   nil nil 23 45 . 89", 2);	
	} 
	public void test3() throws Exception {
		excerciseFormatter("(45 . 89)", "45 . 89", 3);	
	} 
	public void test4() throws Exception {
		excerciseFormatter("(1 2.3 \"str\" (symbol))", "1 2.3 \"str\" (symbol)", 4);	
	} 
	public void test5() throws Exception {
		excerciseFormatter("(1 2 3 4 (23 45))", "1 2 3 4\n   23 45", 2);	
	} 
	public void test6() throws Exception {
		excerciseFormatter("(1 (22) 3)", "1 (22) 3", 2);	
	} 
	public void test7() throws Exception {
		excerciseFormatter("(1 2 3 (44) 5)", "1 2 3\n   44\n   ~ 5", 2);	
	} 
	public void test8() throws Exception {
		excerciseFormatter("(1 2 3 (44) 5 (66) 7 (88) 9)", "1 2 3\n   44\n   ~ 5\n   66\n   ~ 7\n   88\n   ~ 9", 2);	
	} 
}
