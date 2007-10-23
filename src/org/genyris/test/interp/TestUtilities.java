package org.genyris.test.interp;

import java.io.StringWriter;

import org.genyris.core.Exp;
import org.genyris.format.BasicFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

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
