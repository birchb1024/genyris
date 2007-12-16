// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.format;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.genyris.core.Exp;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class RoundtripIndentedFormatterTest extends TestCase {

    void excerciseFormatter(String given) throws Exception {

        // Parse the test statement to a Lisin expression
        Interpreter interpreter = new Interpreter();
        InStream input = new UngettableInStream( new StringInStream(given));
        Parser parser = interpreter.newParser(input);
        Exp expression = parser.read();
        // Output as an indented string
        StringWriter out = new StringWriter();
        Formatter formatter = new IndentedFormatter(out, 3, interpreter);
        expression.acceptVisitor(formatter);
        String formatted = out.getBuffer().toString();
        // Parse the output back into Lisp again
        InStream toParseAgain = new UngettableInStream( new ConvertEofInStream(new IndentStream(new UngettableInStream( new StringInStream(formatted)), false)));
        Parser parser2 = interpreter.newParser(toParseAgain);
        Exp expression2 = parser2.read();

        // The re-read expresion and the original should be the same
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
    public void testFrame1() throws Exception {
        excerciseFormatter("(new (a 1) (b 2) (c 3) (d 4))");
    }

}
