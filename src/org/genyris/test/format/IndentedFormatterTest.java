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
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class IndentedFormatterTest extends TestCase {

    void excerciseFormatter(String given, String expected, int depth) throws Exception {
        Interpreter interpreter = new Interpreter();
        InStream input = new UngettableInStream( new StringInStream(given));
        Parser parser = interpreter.newParser(input);
        Exp expression = parser.read();
        StringWriter out = new StringWriter();
        Formatter formatter = new IndentedFormatter(out, depth, interpreter);

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
    public void test11() throws Exception {
        excerciseFormatter("(1 (symbol) (sym2) (sym3))", "1 (symbol) (sym2)\n   sym3", 3);
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

    public void test9() throws Exception {
        excerciseFormatter("(xxx (a) (b: 2))", "xxx (a) (b : 2)", 3);
        excerciseFormatter("(xxx (a) (b: 2))", "xxx (a) (b : 2)", 3);
        excerciseFormatter("(xxx (a : 1) (b: 2))", "xxx (a : 1) (b : 2)", 3);
        excerciseFormatter("(xxx (a : (1)))", "xxx (a : (1))", 3);
    }
}
