// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import java.io.StringWriter;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.format.BasicFormatter;
import org.genyris.format.Formatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.Parser;

public class TestUtilities {

    public Interpreter _interpreter;

    public TestUtilities() throws GenyrisException {
        _interpreter = new Interpreter();
        _interpreter.init(false);
    }


   public String eval(String script) throws GenyrisException {
        Exp expression = Parser.parseSingleExpressionFromString(_interpreter.getSymbolTable(), script);
        Exp result = _interpreter.evalInGlobalEnvironment(expression);

        StringWriter out = new StringWriter();
        Formatter formatter = new BasicFormatter(out);
        result.acceptVisitor(formatter);
        return out.getBuffer().toString();
    }


}
