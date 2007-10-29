// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import java.io.StringWriter;

import org.genyris.core.Exp;
import org.genyris.format.BasicFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class TestUtilities {
	
	public Interpreter _interpreter;
	
	public TestUtilities() throws GenyrisException {
		_interpreter = new Interpreter();
	}

	public String eval(String script) throws GenyrisException {
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
