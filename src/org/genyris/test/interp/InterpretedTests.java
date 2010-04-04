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
    public void testMath() throws GenyrisException {
        useSourceLoader("mathtests.g");
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
        useSourceLoader("intern-tests.g");
   }
    public void testPrefix() throws GenyrisException {
         useSourceLoader("prefix-tests.g");
    }
    public void testFileIO() throws GenyrisException {
        useSourceLoader("file-tests.g");
   }
    public void testTriples() throws GenyrisException {
        useSourceLoader("triple-tests.g");
    }
    public void testParseAndConvert() throws GenyrisException {
        useSourceLoader("parsetests.g");
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
    public void testExamples() throws GenyrisException {
    	useSourceLoader("examples-as-tests.g");
    }
    public void testWeb() throws GenyrisException {
        useSourceLoader("web-tests.g");
    }
}
