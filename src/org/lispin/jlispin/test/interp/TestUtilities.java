package org.lispin.jlispin.test.interp;

import java.io.StringWriter;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.format.BasicFormatter;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class TestUtilities {
	
	public Interpreter _interpreter;
	
	public TestUtilities() throws LispinException {
		_interpreter = new Interpreter();
	}

	public String eval(String script) throws LispinException {
		InStream input = new UngettableInStream( new StringInStream(script));
		Parser parser = _interpreter.newParser(input);
		Exp expression = parser.read(); 
		Exp result = _interpreter.evalInGlobalEnvironment(expression);
		
		StringWriter out = new StringWriter();
		BasicFormatter formatter = new BasicFormatter(out);
		result.acceptVisitor(formatter);
		return out.getBuffer().toString();
	}

}
