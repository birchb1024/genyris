// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io;

import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.TestCase;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class SourceLoaderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    private void excerciseSourceLoader(String input, String expected) throws GenyrisException {
        Interpreter interp = new Interpreter();
        interp.init(false);
        StringReader in = new StringReader(input);
        StringWriter out = new StringWriter();
        SourceLoader.executeScript(interp.getGlobalEnv(),".lin", interp.getSymbolTable(), in, out);
         assertEquals(expected, out.toString());

    }

    public void testSourceLoader1() throws GenyrisException {
        excerciseSourceLoader("list 1\n list 2\n ~22\n\nlist 3 4","");
        excerciseSourceLoader("list 1\n list 2\n ~22\n\ndef f (x) \n      cons x x","");
        excerciseSourceLoader("list 22\n  list\n      the 333\n\nlist 'f '(x)\n    cons 1 2\n", "");

    }

    public void testSourceLoader2() throws GenyrisException {
        Interpreter interp = new Interpreter();
        interp.init(false);
        StringWriter out = new StringWriter();
        SourceLoader.loadScriptFromClasspath(interp.getGlobalEnv(), interp.getSymbolTable(), "org/genyris/load/boot/init.lin", out);
    }
    public void testSourceLoader3() throws GenyrisException {
        Interpreter interp = new Interpreter();
        interp.init(false);
        StringWriter out = new StringWriter();
        SourceLoader.loadScriptFromClasspath(interp.getGlobalEnv(), interp.getSymbolTable(), "testscripts/factorial.lin", out);
    }
    public void testSourceLoaderLisp() throws GenyrisException {
        Interpreter interp = new Interpreter();
        interp.init(false);
        StringWriter out = new StringWriter();
        SourceLoader.loadScriptFromClasspath(interp.getGlobalEnv(), interp.getSymbolTable(), "testscripts/factorial.lsp", out);
    }
}
