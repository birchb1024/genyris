package org.lispin.jlispin.test.format;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.format.IndentedFormatter;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.io.ConvertEofInStream;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.IndentStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class RoundtripIndentedFormatterTest extends TestCase {

	void excerciseFormatter(String given) throws Exception { 

		Interpreter interpreter = new Interpreter();
		InStream input = new UngettableInStream( new StringInStream(given));
		Parser parser = interpreter.newParser(input);
		Exp expression = parser.read(); 
		StringWriter out = new StringWriter();
		IndentedFormatter formatter = new IndentedFormatter(out, 3);
		expression.acceptVisitor(formatter);
		String formatted = out.getBuffer().toString();

		InStream toParseAgain = new UngettableInStream( new ConvertEofInStream(new IndentStream(new UngettableInStream( new StringInStream(formatted)), false)));
		Parser parser2 = interpreter.newParser(toParseAgain);
		Exp expression2 = parser2.read(); 
		assertEquals(expression.toString(), expression2.toString());
	}

	public void testRountrip2() throws Exception {
		excerciseFormatter("(12)");
	}
	public void testRountrip3() throws Exception {
		excerciseFormatter("(symbol \"string\" 1 2.456 (list a b c d) 4)");
	}
	public void testRountrip4() throws Exception {
		excerciseFormatter("(symbol \"string\" 1 45 (list a b (symbol \"string\" 1 45 (list a b c d) 4) d) 4)");
	}

}
