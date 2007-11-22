// Copyright 2008 Peter William Birch <birchb@genyis.org>
package org.genyris.test.interp;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class InterpretedTests extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    private void useSourceLoader(String filename) throws GenyrisException {
        Interpreter interp = new Interpreter();
        interp.init(true);
        StringWriter out = new StringWriter();
        SourceLoader.loadScriptFromClasspath(interp, filename, out);
    }
    public void testFactorial() throws GenyrisException {
        useSourceLoader("testscripts/factorial.lin");
    }
}
