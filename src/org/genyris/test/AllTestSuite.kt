package org.genyris.test

import junit.framework.Test
import junit.framework.TestSuite
import org.genyris.test.core.*
import org.genyris.test.dl.TripleTest
import org.genyris.test.format.BasicFormatterTest
import org.genyris.test.format.IndentedFormatterTest
import org.genyris.test.format.RoundtripIndentedFormatterTest
import org.genyris.test.interp.*
import org.genyris.test.io.*
import org.genyris.test.string.StringTests


object AllTestSuite {
    fun makeSuite(): Test {
        val suite = TestSuite("AllTestSuite")

        // core
        suite.addTestSuite(CoreTest::class.java)
        suite.addTestSuite(DictTest::class.java)
        suite.addTestSuite(EqualityTest::class.java)
        suite.addTestSuite(LexTest::class.java)
        suite.addTestSuite(LexTestNumbers::class.java)
        suite.addTestSuite(LsymbolTest::class.java)
        suite.addTestSuite(SymbolTableTest::class.java)
        // dl
        suite.addTestSuite(TripleTest::class.java)
        // string
        suite.addTestSuite(StringTests::class.java)

        // format
        suite.addTestSuite(BasicFormatterTest::class.java)
        suite.addTestSuite(IndentedFormatterTest::class.java)
        suite.addTestSuite(RoundtripIndentedFormatterTest::class.java)

        // interp
        suite.addTestSuite(BuiltinInterpreterTests::class.java)
        suite.addTestSuite(ClassTaggingTests::class.java)
        suite.addTestSuite(ComplexInterpreterTests::class.java)
        suite.addTestSuite(EnvironmentTest::class.java)
        suite.addTestSuite(EvalApplyTest::class.java)
        suite.addTestSuite(ObjectOrientationTests::class.java)

        // IO
        suite.addTestSuite(IndentStreamTest::class.java)
        suite.addTestSuite(SourceLoaderTest::class.java)
        suite.addTestSuite(StringFormatStreamTest::class.java)
        suite.addTestSuite(StringInStreamTest::class.java)
        suite.addTestSuite(UngettableInStreamTest::class.java)


        return suite
    }
}
