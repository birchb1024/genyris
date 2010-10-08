// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io;

import junit.framework.TestCase;

import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.LexException;
import org.genyris.io.StringFormatStream;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class StringFormatStreamTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    private void excerciseSFS(String toparse, String expected) throws LexException {
        InStream ind = new ConvertEofInStream(new StringFormatStream(new UngettableInStream(
                new StringInStream(toparse))));
        StringBuffer result = new StringBuffer();
        while (ind.hasData()) {
            result.append(ind.readNext());
        }
        assertEquals(expected, result.toString());
    }



    public void testSFStreamEmpty() throws LexException {
        excerciseSFS("", "\"\"");
    }
    public void testSFStream2() throws LexException {
        excerciseSFS("1", "\"1\"");
    }
    public void testSFStream3() throws LexException {
        excerciseSFS("1", "\"1\"");
    }
    public void testSFStream4() throws LexException {
        excerciseSFS("12343567890", "\"12343567890\"");
    }
    public void testSFStream5() throws LexException {
        excerciseSFS("#{}", "\"\"\"\"");
    }
    public void testSFStream6() throws LexException {
        excerciseSFS("#{alpha}", "\"\"alpha\"\"");
    }
    public void testSFStream7() throws LexException {
        excerciseSFS("one#{alpha}two", "\"one\"alpha\"two\"");
    }

    public void testSFStream8() throws LexException {
        excerciseSFS("on\"e#{alpha}two", "\"on\\\"e\"alpha\"two\"");
    }

    public void testSFStreamReal() throws LexException {
        excerciseSFS("<img src=\"#{image-url-var}\">", "\"<img src=\\\"\"image-url-var\"\\\">\"");
    }
    public void testSFStreamMultiple() throws LexException {
        excerciseSFS("12#{3}435#{6}78#{9}0", "\"12\"3\"435\"6\"78\"9\"0\"");
    }

}
