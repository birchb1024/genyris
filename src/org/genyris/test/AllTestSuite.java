package org.genyris.test;


import junit.framework.Test;
import junit.framework.TestSuite;
import org.genyris.test.core.*;
import org.genyris.test.format.*;
import org.genyris.test.interp.BuiltinInterpreterTests;
import org.genyris.test.interp.ClassTaggingTests;
import org.genyris.test.interp.ComplexInterpreterTests;
import org.genyris.test.interp.EnvironmentTest;
import org.genyris.test.interp.EvalApplyTest;
import org.genyris.test.interp.InterpretedTests;
import org.genyris.test.interp.ObjectOrientationTests;
import org.genyris.test.io.IndentStreamTest;
import org.genyris.test.io.SourceLoaderTest;
import org.genyris.test.io.StringInStreamTest;
import org.genyris.test.io.UngettableInStreamTest;

public class AllTestSuite {

    public static Test makeSuite() {

        TestSuite suite = new TestSuite("AllTestSuite");

        suite.addTestSuite(CoreTest.class);
        suite.addTestSuite(DictTest.class);
        suite.addTestSuite(EqualityTest.class);
        suite.addTestSuite(LexTest.class);
        suite.addTestSuite(LexTestNumbers.class);
        suite.addTestSuite(LsymbolTest.class);

        suite.addTestSuite(BasicFormatterTest.class);
        suite.addTestSuite(IndentedFormatterTest.class);
        suite.addTestSuite(RoundtripIndentedFormatterTest.class);

        suite.addTestSuite(BuiltinInterpreterTests.class);
        suite.addTestSuite(ClassTaggingTests.class);
        suite.addTestSuite(ComplexInterpreterTests.class);
        suite.addTestSuite(EnvironmentTest.class);
        suite.addTestSuite(EvalApplyTest.class);
        suite.addTestSuite(InterpretedTests.class);
        suite.addTestSuite(ObjectOrientationTests.class);

        suite.addTestSuite(IndentStreamTest.class);
        suite.addTestSuite(SourceLoaderTest.class);
        suite.addTestSuite(StringInStreamTest.class);
        suite.addTestSuite(UngettableInStreamTest.class);


        return suite;
    }
}
