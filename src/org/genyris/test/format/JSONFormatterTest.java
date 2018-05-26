// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.format;

import junit.framework.TestCase;
import org.genyris.core.Exp;
import org.genyris.format.Formatter;
import org.genyris.format.JSONFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

import java.io.StringWriter;

public class JSONFormatterTest extends TestCase {

    void excerciseFormatter(String given, String expected) throws Exception {
        Interpreter interpreter = new Interpreter();
        InStream input = new UngettableInStream( new StringInStream(given));
        Parser parser = interpreter.newParser(input);
        Exp expression = parser.read();
        StringWriter out = new StringWriter();
        Formatter formatter = new JSONFormatter(out);

        expression.acceptVisitor(formatter);

        assertEquals(expected, out.getBuffer().toString());

    }

    public void test1() throws Exception {
        excerciseFormatter("(1 2.3 'str' (symbol))", "[ 1 , 2.3 , \"str\" , [ \"symbol\" ] ]");
    }
    public void test2() throws Exception {
        excerciseFormatter("(1 2.3 (nil nil 2e3 45 89))", "[ 1 , 2.3 , [ [] , [] , 2000 , 45 , 89 ] ]");
    }
    public void test3() throws Exception {
        excerciseFormatter("(45 89)", "[ 45 , 89 ]");
    }
    public void testcdr() throws Exception {
        excerciseFormatter("(45 = 89)", "[ 45 , 89 ]");
    }
    public void testSymbolsStrange() throws Exception {
        excerciseFormatter("(|foo| |\\\" | .|jhg| )",
                "[ \"foo\" , \"\\\" \" , \"jhg\" ]");
    }
    public void testSymbols() throws Exception {
        excerciseFormatter("(quux |fo:o| |http://foo/bar#| .|http://www.genyris.org/lang/system#foo|)",
        		"[ \"quux\" , \"fo:o\" , \"http://foo/bar#\" , \"http://www.genyris.org/lang/system#foo\" ]");
    }
}
