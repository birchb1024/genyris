package org.genyris;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.genyris.core.CoreTest;
import org.genyris.core.DictTest;
import org.genyris.core.EqualityTest;
import org.genyris.core.LexTest;
import org.genyris.core.LexTestNumbers;
import org.genyris.core.LsymbolTest;
import org.genyris.core.SymbolTableTest;
import org.genyris.dl.TripleTest;
import org.genyris.format.BasicFormatterTest;
import org.genyris.format.IndentedFormatterTest;
import org.genyris.format.RoundtripIndentedFormatterTest;
import org.genyris.interp.BuiltinInterpreterTests;
import org.genyris.interp.ClassTaggingTests;
import org.genyris.interp.ComplexInterpreterTests;
import org.genyris.interp.EnvironmentTest;
import org.genyris.interp.EvalApplyTest;
import org.genyris.interp.ObjectOrientationTests;
import org.genyris.io.IndentStreamTest;
import org.genyris.io.SourceLoaderTest;
import org.genyris.io.StringFormatStreamTest;
import org.genyris.io.StringInStreamTest;
import org.genyris.io.UngettableInStreamTest;
import org.genyris.string.StringTests;

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
