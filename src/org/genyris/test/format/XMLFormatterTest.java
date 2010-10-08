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
import org.genyris.format.HTMLFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class XMLFormatterTest extends TestCase {

    void excerciseFormatter(String given, String expected) throws Exception {
        Interpreter interpreter = new Interpreter();
        InStream input = new UngettableInStream( new StringInStream(given));
        Parser parser = interpreter.newParser(input);
        Exp expression = parser.read();
        StringWriter out = new StringWriter();
        Formatter formatter = new HTMLFormatter(out);

        expression.acceptVisitor(formatter);

        assertEquals(expected, out.getBuffer().toString());

    }

    public void test1() throws Exception {
        excerciseFormatter("(1 2.3 'str' (symbol))", "12.3str<symbol/>");
    }
    public void test2() throws Exception {
        excerciseFormatter("(1 2.3 (nil nil 23 45 89))", "12.3<nil>234589</nil>");
    }
    public void test3() throws Exception {
        excerciseFormatter("(45 89)", "4589");
    }
    public void testSymbols() throws Exception {
        excerciseFormatter("(quux |fo:o| |http://foo/bar#| .|http://www.genyris.org/lang/system#foo|)",
        		"<quux *** error bad HTML attribute: |fo:o|><http://foo/bar# *** error bad HTML attribute: .|http://www.genyris.org/lang/system#foo|/></quux>");
    }
    public void testPage() throws Exception {
        excerciseFormatter("(html((base='foo')(quux='bar'))(body()'text'(img((src='http://foo/')))))", 
        		"<html base=\"foo\" quux=\"bar\"><body>text<img src=\"http://foo/\"/></body></html>");
        excerciseFormatter("(verbatim() '&nbsp;<ww><www></ww>')",  "&nbsp;<ww><www></ww>");
    }
}
