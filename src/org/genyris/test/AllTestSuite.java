package org.genyris.test;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.genyris.test.core.CoreTest;
import org.genyris.test.core.DictTest;
import org.genyris.test.core.EqualityTest;
import org.genyris.test.core.LexTest;
import org.genyris.test.core.LexTestNumbers;
import org.genyris.test.core.LsymbolTest;
import org.genyris.test.core.SymbolTableTest;
import org.genyris.test.dl.GraphTest;
import org.genyris.test.dl.TripleTest;
import org.genyris.test.format.BasicFormatterTest;
import org.genyris.test.format.IndentedFormatterTest;
import org.genyris.test.format.RoundtripIndentedFormatterTest;
import org.genyris.test.interp.BuiltinInterpreterTests;
import org.genyris.test.interp.ClassTaggingTests;
import org.genyris.test.interp.ComplexInterpreterTests;
import org.genyris.test.interp.EnvironmentTest;
import org.genyris.test.interp.EvalApplyTest;
import org.genyris.test.interp.ObjectOrientationTests;
import org.genyris.test.io.IndentStreamTest;
import org.genyris.test.io.SourceLoaderTest;
import org.genyris.test.io.StringFormatStreamTest;
import org.genyris.test.io.StringInStreamTest;
import org.genyris.test.io.UngettableInStreamTest;
import org.genyris.test.string.StringTests;

public class AllTestSuite {

    public static Test makeSuite() {

        TestSuite suite = new TestSuite("AllTestSuite");

        // core
        suite.addTestSuite(CoreTest.class);
        suite.addTestSuite(DictTest.class);
        suite.addTestSuite(EqualityTest.class);
        suite.addTestSuite(LexTest.class);
        suite.addTestSuite(LexTestNumbers.class);
        suite.addTestSuite(LsymbolTest.class);
        suite.addTestSuite(SymbolTableTest.class);
        // dl
        suite.addTestSuite(GraphTest.class);
        suite.addTestSuite(TripleTest.class);
        // string
        suite.addTestSuite(StringTests.class);

        // format
        suite.addTestSuite(BasicFormatterTest.class);
        suite.addTestSuite(IndentedFormatterTest.class);
        suite.addTestSuite(RoundtripIndentedFormatterTest.class);

        // interp
        suite.addTestSuite(BuiltinInterpreterTests.class);
        suite.addTestSuite(ClassTaggingTests.class);
        suite.addTestSuite(ComplexInterpreterTests.class);
        suite.addTestSuite(EnvironmentTest.class);
        suite.addTestSuite(EvalApplyTest.class);
        suite.addTestSuite(ObjectOrientationTests.class);

        // IO
        suite.addTestSuite(IndentStreamTest.class);
        suite.addTestSuite(SourceLoaderTest.class);
        suite.addTestSuite(StringFormatStreamTest.class);
        suite.addTestSuite(StringInStreamTest.class);
        suite.addTestSuite(UngettableInStreamTest.class);


        return suite;
    }
}
