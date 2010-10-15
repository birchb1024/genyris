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
        interp.init(false);
        StringWriter out = new StringWriter();
        try {
            SourceLoader.loadScriptFromClasspath(interp.getGlobalEnv(), interp.getSymbolTable(), "testscripts/" + filename, out);
        } finally {
            System.out.println(out.getBuffer());
        }
    }
    public void testCoverage() throws GenyrisException {
        useSourceLoader("test-coverage.g");
    }
    public void testMath() throws GenyrisException {
        useSourceLoader("test-math.g");
    }
    public void testLogic() throws GenyrisException {
        useSourceLoader("test-logic.g");
    }
    public void testFactorial() throws GenyrisException {
        useSourceLoader("factorial.g");
    }
    public void testTak() throws GenyrisException {
        useSourceLoader("tak.g");
    }
    public void testDefects() throws GenyrisException {
        useSourceLoader("defects.g");
    }
    public void testIntern() throws GenyrisException {
        useSourceLoader("test-intern.g");
   }
    public void testPrefix() throws GenyrisException {
         useSourceLoader("test-prefix.g");
    }
    public void testFileIO() throws GenyrisException {
        useSourceLoader("file-tests.g");
   }
    public void testMacros() throws GenyrisException {
        useSourceLoader("test-macros.g");
    }
    public void testTriples() throws GenyrisException {
        useSourceLoader("triple-tests.g");
    }
    public void testParseAndConvert() throws GenyrisException {
        useSourceLoader("test-parse.g");
    }
    public void testCSV() throws GenyrisException {
        useSourceLoader("csv-tests.g");
    }
    public void testJava() throws GenyrisException {
        useSourceLoader("test-java.g");
    }
    public void testManual() throws GenyrisException {
        useSourceLoader("manual.g");
    }
    public void testMiscellaneous() throws GenyrisException {
    	useSourceLoader("miscellaneous-tests.g");
    }
    public void testImport() throws GenyrisException {
    	useSourceLoader("test-import.g");
    }
    public void testInclude() throws GenyrisException {
    	useSourceLoader("test-include.g");
    }
     public void testExamples() throws GenyrisException {
    	useSourceLoader("examples-as-tests.g");
    }
    public void testPipes() throws GenyrisException {
        useSourceLoader("test-pipe.g");
    }
    public void testWeb() throws GenyrisException {
        useSourceLoader("test-web.g");
    }
    public void testTime() throws GenyrisException {
        useSourceLoader("test-time.g");
    }
    public void testIterators() throws GenyrisException {
        useSourceLoader("test-iterators.g");
    }
}
